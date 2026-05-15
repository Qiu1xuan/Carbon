package com.qms.carbon.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Service.CarbonRecordService;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class CarbonRecordController {

    @Autowired
    private CarbonRecordService recordService;

    /**
     * 1. 行为上报接口 (支持补报历史数据)
     * POST /api/record/report
     * 参数示例: { "userId": 1, "type": "WALK", "value": 5000, "occurTime": "2023-10-01T10:00:00" }
     */
    @PostMapping("/report")
    public Result<CarbonRecord> report(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String type = (String) params.get("type");
        BigDecimal value = new BigDecimal(params.get("value").toString());

        // 处理时间：如果前端传了时间就解析，没传就用当前时间
        LocalDateTime occurTime = null;
        if (params.containsKey("occurTime") && params.get("occurTime") != null) {
            occurTime = LocalDateTime.parse(params.get("occurTime").toString());
        }

        CarbonRecord record = recordService.reportAction(userId, type, value, occurTime);
        return Result.success(record);
    }

    /**
     * 2. 获取看板概览数据 (Dashboard)
     * GET /api/record/overview/{userId}
     * 返回：今日减排、总减排、总积分
     */
    @GetMapping("/overview/{userId}")
    public Result<Map<String, Object>> getOverview(@PathVariable Long userId) {
        return Result.success(recordService.getDashboardOverview(userId));
    }

    /**
     * 3. 获取个人统计趋势 (用于鸿蒙图表绘制)
     * GET /api/record/stats?userId=1&range=week
     * range 可选值: week, month, year
     */
    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getStats(@RequestParam Long userId,
                                                      @RequestParam(defaultValue = "week") String range) {
        return Result.success(recordService.getChartStats(userId, range));
    }

    /**
     * 4. 分页获取个人历史记录
     * GET /api/record/history?userId=1&current=1&size=10
     */
    @GetMapping("/history")
    public Result<Page<CarbonRecord>> getHistory(@RequestParam Long userId,
                                                 @RequestParam(defaultValue = "1") int current,
                                                 @RequestParam(defaultValue = "10") int size) {
        return Result.success(recordService.getHistoryPage(userId, current, size));
    }

    /**
     * 5. 排行榜接口
     * GET /api/record/rank?city=上海&range=week
     */
    @GetMapping("/rank")
    public Result<List<UserRankVO>> getRank(@RequestParam(required = false) String city,
                                            @RequestParam(defaultValue = "all") String range) {
        return Result.success(recordService.getRankList(city, range));
    }

    /**
     * 6. 删除记录 (逻辑删除)
     * DELETE /api/record/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        recordService.removeById(id);
        return Result.success("记录已成功删除");
    }

    /**
     * 7. 修改记录备注
     * PUT /api/record/{id}
     */
    @PutMapping("/{id}")
    public Result<String> updateRemark(@PathVariable Long id, @RequestBody Map<String, String> body) {
        CarbonRecord record = new CarbonRecord();
        record.setId(id);
        record.setRemark(body.get("remark"));
        recordService.updateById(record);
        return Result.success("备注修改成功");
    }
}