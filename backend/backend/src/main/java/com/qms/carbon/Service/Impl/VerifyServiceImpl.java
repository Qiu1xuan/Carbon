package com.qms.carbon.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.carbon.Common.HashUtil;
import com.qms.carbon.Common.MerkleTreeUtil;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Entity.MerkleDailySummary;
import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Mapper.MerkleDailySummaryMapper;
import com.qms.carbon.Service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VerifyServiceImpl extends ServiceImpl<MerkleDailySummaryMapper, MerkleDailySummary> implements VerifyService {

    @Autowired
    private CarbonRecordMapper carbonRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerkleDailySummary generateDailyMerkleRoot(LocalDate date) {
        // 1. 获取当天的所有减排记录
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<CarbonRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(CarbonRecord::getRecordTime, startTime)
                .le(CarbonRecord::getRecordTime, endTime)
                .eq(CarbonRecord::getDeleted, 0);

        List<CarbonRecord> dailyRecords = carbonRecordMapper.selectList(queryWrapper);

        if (dailyRecords.isEmpty()) {
            throw new RuntimeException("该日期没有减排记录，无法生成 Merkle Root");
        }

        // 2. 提取所有记录的 merkleHash，如果为空则动态计算并更新
        List<String> hashes = dailyRecords.stream().map(record -> {
            if (record.getMerkleHash() == null) {
                // 模拟根据记录核心数据生成单条哈希
                String rawData = record.getUserId() + "_" + record.getActivityType() + "_" + record.getReduction();
                String hash = HashUtil.sha256(rawData);
                record.setMerkleHash(hash);
                carbonRecordMapper.updateById(record);
                return hash;
            }
            return record.getMerkleHash();
        }).collect(Collectors.toList());

        // 3. 调用算法计算当天的 Merkle Root
        String merkleRoot = MerkleTreeUtil.getMerkleRoot(hashes);

        // 4. 计算当天总减排量
        long totalReduction = dailyRecords.stream().mapToLong(CarbonRecord::getReduction).sum();

        // 5. 保存摘要到数据库 (先查是否已存在，存在则更新，不存在则插入)
        LambdaQueryWrapper<MerkleDailySummary> summaryQuery = new LambdaQueryWrapper<>();
        summaryQuery.eq(MerkleDailySummary::getRecordDate, date);
        MerkleDailySummary summary = this.getOne(summaryQuery);

        if (summary == null) {
            summary = new MerkleDailySummary();
            summary.setRecordDate(date);
        }

        summary.setMerkleRoot(merkleRoot);
        summary.setRecordCount(dailyRecords.size());
        summary.setTotalReduction(totalReduction);

        this.saveOrUpdate(summary);

        // 6. 把生成的 root 回写到当天的每一条记录中（标志着被正式确权存证）
        for (CarbonRecord record : dailyRecords) {
            record.setMerkleRoot(merkleRoot);
            carbonRecordMapper.updateById(record);
        }

        return summary;
    }

    @Override
    public boolean verifyRecordIntegrity(Long recordId) {
        CarbonRecord record = carbonRecordMapper.selectById(recordId);
        if (record == null) return false;

        // 重新计算哈希看是否与数据库存储的一致
        String rawData = record.getUserId() + "_" + record.getActivityType() + "_" + record.getReduction();
        String currentHash = HashUtil.sha256(rawData);

        return currentHash.equals(record.getMerkleHash());
    }
}