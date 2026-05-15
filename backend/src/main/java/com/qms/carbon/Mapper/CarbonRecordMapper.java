package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Vo.UserRankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CarbonRecordMapper extends BaseMapper<CarbonRecord> {

    // 个人概览数据：今日、总计
    @Select("SELECT " +
            "IFNULL(SUM(reduction), 0) as totalReduction, " +
            "IFNULL(SUM(points_earned), 0) as totalPoints, " +
            "(SELECT IFNULL(SUM(reduction), 0) FROM carbon_record WHERE user_id = #{userId} AND TO_DAYS(record_time) = TO_DAYS(NOW()) AND deleted=0) as todayReduction " +
            "FROM carbon_record WHERE user_id = #{userId} AND deleted = 0")
    Map<String, Object> getUserOverview(@Param("userId") Long userId);

    // 多维度统计：支持周(week)、月(month)、年(year)视图
    @Select("<script>" +
            "SELECT " +
            "<choose>" +
            "  <when test='range == \"year\"'> DATE_FORMAT(record_time, '%Y-%m') </when>" +
            "  <otherwise> DATE_FORMAT(record_time, '%Y-%m-%d') </otherwise>" +
            "</choose> AS dateLabel, " +
            "SUM(reduction) AS totalValue " +
            "FROM carbon_record " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "AND record_time >= " +
            "<choose>" +
            "  <when test='range == \"week\"'> DATE_SUB(CURDATE(), INTERVAL 7 DAY) </when>" +
            "  <when test='range == \"month\"'> DATE_SUB(CURDATE(), INTERVAL 30 DAY) </when>" +
            "  <otherwise> DATE_SUB(CURDATE(), INTERVAL 1 YEAR) </otherwise>" +
            "</choose>" +
            "GROUP BY dateLabel ORDER BY dateLabel ASC" +
            "</script>")
    List<Map<String, Object>> getStatsByRange(@Param("userId") Long userId, @Param("range") String range);


    /**
     * 【大一统排行接口】：获取个人排行榜 (支持按积分排序)
     *
     * @param city      如果传了城市名，就是【同城排行榜】；如果不传，跳过该条件
     * @param orgId     如果传了企业ID，就是【同企业排行榜】；如果不传，跳过该条件
     *                  如果两都不传，就是【全平台排行榜】
     * @param startDate 开始时间（用于查询月榜、周榜）
     * @param endDate   结束时间
     */
    @Select("<script>" +
            "SELECT u.id AS userId, u.real_name AS realName, u.avatar, u.city, " +
            "IFNULL(SUM(cr.reduction), 0) AS totalReduction, " +
            "IFNULL(SUM(cr.points_earned), 0) AS totalPoints " + // 聚合积分
            "FROM user u " +
            "LEFT JOIN carbon_record cr ON u.id = cr.user_id AND cr.deleted = 0 " +
            "<if test='startDate != null'> AND cr.record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND cr.record_time &lt;= #{endDate} </if>" +
            "WHERE u.deleted = 0 AND u.status = 1 " +
            "<if test='city != null and city != \"\"'> AND u.city = #{city} </if>" +
            "<if test='orgId != null'> AND u.org_id = #{orgId} </if>" +
            "GROUP BY u.id, u.real_name, u.avatar, u.city " +
            "ORDER BY totalPoints DESC, totalReduction DESC " + // 优先按积分排，同分按减排量排
            "</script>")
    List<UserRankVO> getPersonalPointsRank(@Param("city") String city,
                                           @Param("orgId") Long orgId,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate);

    // 统计个人最近7天的每日减排量总和（克）
    @Select("SELECT DATE(record_time) as date, SUM(reduction) as total " +
            "FROM carbon_record " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "GROUP BY DATE(record_time) ORDER BY date ASC LIMIT 7")
    List<Map<String, Object>> getWeeklyStats(Long userId);


}