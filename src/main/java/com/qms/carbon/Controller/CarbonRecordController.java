package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Service.CarbonRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class CarbonRecordController {

    @Autowired
    private CarbonRecordService recordService;

    /**
     * 上报行为接口
     * 示例：Get /api/record/report?userId=2&type=WALK&value=5000
     */
    @GetMapping("/report")
    public Result<CarbonRecord> report(@RequestParam Long userId,
                                       @RequestParam String type,
                                       @RequestParam BigDecimal value) {
        CarbonRecord record = recordService.reportAction(userId, type, value);
        return Result.success(record);
    }

    /**
     * 获取周统计数据（看板用）
     */
    @GetMapping("/stats/{userId}")
    public Result<List<Map<String, Object>>> stats(@PathVariable Long userId) {
        return Result.success(recordService.getWeeklyStats(userId));
    }

    /**
     * 获取个人历史列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<CarbonRecord>> list(@PathVariable Long userId) {
        List<CarbonRecord> list = recordService.lambdaQuery()
                .eq(CarbonRecord::getUserId, userId)
                .orderByDesc(CarbonRecord::getRecordTime)
                .list();
        return Result.success(list);
    }
}