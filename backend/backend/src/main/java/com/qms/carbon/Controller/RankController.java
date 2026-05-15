package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Service.RankService;
import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 碳普惠排行榜接口
 */
@RestController
@RequestMapping("/api/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    /**
     * 1. 获取全平台【企业总减排量】排行榜 (聚合汇总)
     * GET /api/rank/enterprise/global?startDate=2026-05-01&endDate=2026-05-31
     * (时间参数可选，不传则查所有历史数据)
     */
    @GetMapping("/enterprise/global")
    public Result<List<EnterpriseRankVO>> getEnterpriseGlobalRank(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<EnterpriseRankVO> list = rankService.getEnterpriseGlobalRank(startDate, endDate);
        return Result.success(list);
    }

    /**
     * 2. 获取全平台【个人积分】排行榜
     * GET /api/rank/personal/global
     */
    @GetMapping("/personal/global")
    public Result<List<UserRankVO>> getGlobalPersonalRank(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<UserRankVO> list = rankService.getGlobalPersonalRank(startDate, endDate);
        return Result.success(list);
    }

    /**
     * 3. 获取同城【个人积分】排行榜
     * GET /api/rank/personal/city?city=上海
     */
    @GetMapping("/personal/city")
    public Result<List<UserRankVO>> getCityPersonalRank(
            @RequestParam String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<UserRankVO> list = rankService.getCityPersonalRank(city, startDate, endDate);
        return Result.success(list);
    }

    /**
     * 4. 获取同企业内部【个人积分/减排】排行榜
     * GET /api/rank/personal/enterprise?orgId=1
     */
    @GetMapping("/personal/enterprise")
    public Result<List<UserRankVO>> getEnterprisePersonalRank(
            @RequestParam Long orgId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<UserRankVO> list = rankService.getEnterprisePersonalRank(orgId, startDate, endDate);
        return Result.success(list);
    }
}