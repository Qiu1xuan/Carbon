package com.qms.carbon.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Vo.UserRankVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 组员 B 任务：减排记录服务接口
 */
public interface CarbonRecordService extends IService<CarbonRecord> {

    /**
     * 1. 核心核算方法 (实现动态核算)
     * @param userId 用户ID
     * @param activityType 行为类型 (WALK, BIKE等)
     * @param activityValue 原始数值 (步数, 公里数等)
     * @param occurTime 行为发生时间 (支持补报历史数据，用于匹配当时的因子版本)
     * @return 核算完成后的记录对象
     */
    CarbonRecord reportAction(Long userId, String activityType, BigDecimal activityValue, LocalDateTime occurTime);

    /**
     * 2. 个人仪表盘数据汇总
     * 包含：今日减排量、累计总减排量、累计总积分
     * @return Map 包含 todayReduction, totalReduction, totalPoints
     */
    Map<String, Object> getDashboardOverview(Long userId);

    /**
     * 3. 统计曲线数据 (用于鸿蒙图表展示)
     * @param range 统计范围：'week' (近7天), 'month' (近30天), 'year' (按月汇总)
     * @return 包含日期(dateLabel)和数值(totalValue)的列表
     */
    List<Map<String, Object>> getChartStats(Long userId, String range);

    /**
     * 4. 分页获取个人历史记录
     * @param current 当前页
     * @param size 每页大小
     */
    Page<CarbonRecord> getHistoryPage(Long userId, int current, int size);

    /**
     * 5. 排行榜业务 (调用队长写的 Mapper)
     * @param city 城市筛选 (可选)
     * @param range 时间范围 (week, month, all)
     */
    List<UserRankVO> getRankList(String city, String range);
}