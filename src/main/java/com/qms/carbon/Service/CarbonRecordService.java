package com.qms.carbon.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.carbon.Entity.CarbonRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CarbonRecordService extends IService<CarbonRecord> {
    // 用户行为上报核算核心方法
    CarbonRecord reportAction(Long userId, String activityType, BigDecimal activityValue);

    // 获取个人周统计
    List<Map<String, Object>> getWeeklyStats(Long userId);
}