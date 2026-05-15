package com.qms.carbon.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.carbon.Common.HashUtil;
import com.qms.carbon.Entity.CarbonFactorLog;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Mapper.CarbonFactorLogMapper;
import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Service.CarbonRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CarbonRecordServiceImpl extends ServiceImpl<CarbonRecordMapper, CarbonRecord> implements CarbonRecordService {

    @Autowired
    private CarbonFactorLogMapper factorLogMapper;

    @Override
    @Transactional
    public CarbonRecord reportAction(Long userId, String activityType, BigDecimal activityValue) {
        // 1. 查找当前有效的因子 (适配你的表字段 activity_type 和 start_time)
        LocalDateTime now = LocalDateTime.now();
        CarbonFactorLog factor = factorLogMapper.selectOne(new LambdaQueryWrapper<CarbonFactorLog>()
                .eq(CarbonFactorLog::getActivityType, activityType)
                .le(CarbonFactorLog::getStartTime, now)
                .and(w -> w.ge(CarbonFactorLog::getEndTime, now).or().isNull(CarbonFactorLog::getEndTime))
                .last("LIMIT 1"));

        if (factor == null) throw new RuntimeException("核算失败：未找到当前有效的因子标准");

        // 2. 计算减排量 (克)
        // 计算公式：数值 * 因子值。结果四舍五入转为 Long
        BigDecimal resultGrams = activityValue.multiply(factor.getFactor());
        long reductionGrams = resultGrams.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();

        // 3. 计算积分 (按你的SQL逻辑：100克=1积分)
        int points = (int) (reductionGrams / 100);

        // 4. 组装 Entity (完全适配队长给的字段)
        CarbonRecord record = new CarbonRecord();
        record.setUserId(userId)
                .setActivityType(activityType)
                .setActivityValue(activityValue)
                .setReduction(reductionGrams)
                .setPointsEarned(points)
                .setRecordTime(now)
                .setFactorVersion(factor.getVersion())
                .setFactorValue(factor.getFactor())
                .setVerifyStatus(1) // 默认已核算
                .setSource("MANUAL")
                .setDeleted(0);

        // 5. 调用队长给的 HashUtil 进行数据存证
        String signStr = userId + activityType + reductionGrams + now.toString();
        record.setMerkleHash(HashUtil.sha256(signStr));

        // 6. 保存并返回
        this.save(record);
        return record;
    }

    @Override
    public List<Map<String, Object>> getWeeklyStats(Long userId) {
        return this.baseMapper.getWeeklyStats(userId);
    }
}