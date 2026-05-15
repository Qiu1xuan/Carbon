package com.qms.carbon.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.carbon.Common.HashUtil;
import com.qms.carbon.Entity.CarbonFactorLog;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Mapper.CarbonFactorLogMapper;
import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Service.CarbonRecordService;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 组员 B 任务实现：核算引擎与个人减排业务
 */
@Service
public class CarbonRecordServiceImpl extends ServiceImpl<CarbonRecordMapper, CarbonRecord> implements CarbonRecordService {

    @Autowired
    private CarbonFactorLogMapper factorLogMapper;

    /**
     * 1. 核心核算引擎 (calculateReduction 逻辑)
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonRecord reportAction(Long userId, String activityType, BigDecimal activityValue, LocalDateTime occurTime) {

        // 如果没有传入发生时间，默认为当前时间
        // 引入一个 final 变量用于 Lambda 表达式内部
        final LocalDateTime finalTime = (occurTime == null) ? LocalDateTime.now() : occurTime;

        // 【动态核算逻辑】：使用 finalTime 进行查询
        CarbonFactorLog factor = factorLogMapper.selectOne(new LambdaQueryWrapper<CarbonFactorLog>()
                .eq(CarbonFactorLog::getActivityType, activityType)
                .le(CarbonFactorLog::getStartTime, finalTime) // 生效时间 <= 发生时间
                .and(w -> w.ge(CarbonFactorLog::getEndTime, finalTime) // 使用 final 变量
                        .or().isNull(CarbonFactorLog::getEndTime))
                .last("LIMIT 1"));

        if (factor == null) {
            throw new RuntimeException("核算失败：该时间点[" + finalTime + "]没有对应的碳因子标准");
        }

        // 计算减排量 (克)：数值 * 因子
        BigDecimal reductionResult = activityValue.multiply(factor.getFactor());
        long reductionGrams = reductionResult.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();

        // 计算积分：100克 = 1积分
        int points = (int) (reductionGrams / 100);

        // 构造记录对象
        CarbonRecord record = new CarbonRecord();
        record.setUserId(userId)
                .setActivityType(activityType)
                .setActivityValue(activityValue)
                .setReduction(reductionGrams)
                .setPointsEarned(points)
                .setRecordTime(finalTime)
                .setFactorVersion(factor.getVersion())
                .setFactorValue(factor.getFactor())
                .setVerifyStatus(1)
                .setSource("APP_REPORT")
                .setDeleted(0);

        // 数据存证：sha256
        String signStr = userId + activityType + reductionGrams + finalTime.toString();
        record.setMerkleHash(HashUtil.sha256(signStr));

        this.save(record);
        return record;
    }

    /**
     * 2. 仪表盘概览数据
     */
    @Override
    public Map<String, Object> getDashboardOverview(Long userId) {
        return this.baseMapper.getUserOverview(userId);
    }

    /**
     * 3. 个人统计曲线数据 (周/月/年)
     */
    @Override
    public List<Map<String, Object>> getChartStats(Long userId, String range) {
        return this.baseMapper.getStatsByRange(userId, range);
    }

    /**
     * 4. 个人历史记录 (分页)
     */
    @Override
    public Page<CarbonRecord> getHistoryPage(Long userId, int current, int size) {
        Page<CarbonRecord> page = new Page<>(current, size);
        return this.lambdaQuery()
                .eq(CarbonRecord::getUserId, userId)
                .orderByDesc(CarbonRecord::getRecordTime)
                .page(page);
    }

    /**
     * 5. 排行榜
     */
    @Override
    public List<UserRankVO> getRankList(String city, String range) {
        String startDate = null;
        if ("week".equals(range)) {
            startDate = LocalDateTime.now().minusDays(7).toString();
        } else if ("month".equals(range)) {
            startDate = LocalDateTime.now().minusDays(30).toString();
        }
        return this.baseMapper.getPersonalPointsRank(city, null, startDate, null);
    }
}