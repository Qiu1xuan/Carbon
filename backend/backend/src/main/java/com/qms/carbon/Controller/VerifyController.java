package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.MerkleDailySummary;
import com.qms.carbon.Service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    @Autowired
    private VerifyService verifyService;

    /**
     * 手动触发生成指定日期的 Merkle Root (实际项目中通常由定时任务晚上12点自动跑)
     * 示例：GET /api/verify/generateRoot?date=2023-10-25
     */
    @GetMapping("/generateRoot")
    public Result<MerkleDailySummary> generateDailyRoot(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            MerkleDailySummary summary = verifyService.generateDailyMerkleRoot(date);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 校验单条记录是否被篡改
     * 示例：GET /api/verify/check/1
     */
    @GetMapping("/check/{id}")
    public Result<Boolean> verifyRecord(@PathVariable("id") Long id) {
        boolean isValid = verifyService.verifyRecordIntegrity(id);
        if (isValid) {
            return Result.success(true); // 校验通过，数据可信
        } else {
            return Result.error(500, "警告：该条记录哈希不匹配，数据可能已被篡改！");
        }
    }
}