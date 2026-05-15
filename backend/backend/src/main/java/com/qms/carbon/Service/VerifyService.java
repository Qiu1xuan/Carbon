package com.qms.carbon.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.carbon.Entity.MerkleDailySummary;
import java.time.LocalDate;

public interface VerifyService extends IService<MerkleDailySummary> {
    /**
     * 生成指定日期的默克尔根并保存到摘要表
     * @param date 指定日期
     * @return 生成的默克尔摘要信息
     */
    MerkleDailySummary generateDailyMerkleRoot(LocalDate date);

    /**
     * 校验单条减排记录的数据完整性
     * @param recordId 记录ID
     * @return true 表示数据未被篡改，false 表示被篡改
     */
    boolean verifyRecordIntegrity(Long recordId);
}