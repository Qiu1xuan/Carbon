package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarbonRecordMapper extends BaseMapper<CarbonRecord> {

    // ==========================================
    // 1. 企业排行榜系列 SQL
    // ==========================================

    /**
     * 1. 统计企业内部人员的减排排行榜 (按减排量聚合)
     */
    @Select("<script>" +
            "SELECT u.id AS userId, u.real_name AS realName, u.avatar, " +
            "IFNULL(SUM(cr.reduction), 0) AS totalReduction " +
            "FROM user u " +
            "LEFT JOIN carbon_record cr ON u.id = cr.user_id AND cr.deleted = 0 " +
            "<if test='startDate != null'> AND cr.record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND cr.record_time &lt;= #{endDate} </if>" +
            "WHERE u.org_id = #{orgId} AND u.deleted = 0 " +
            "GROUP BY u.id, u.real_name, u.avatar " +
            "ORDER BY totalReduction DESC" +
            "</script>")
    List<UserRankVO> getEnterpriseInternalRank(@Param("orgId") Long orgId,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate);

    /**
     * 2. 统计所有企业的减排量汇总 (全平台企业总榜)
     */
    @Select("<script>" +
            "SELECT e.id AS enterpriseId, e.name AS enterpriseName, " +
            "IFNULL(SUM(cr.reduction), 0) AS totalReduction " +
            "FROM enterprise e " +
            "JOIN user u ON e.id = u.org_id AND u.deleted = 0 " +
            "JOIN carbon_record cr ON u.id = cr.user_id AND cr.deleted = 0 " +
            "<if test='startDate != null'> AND cr.record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND cr.record_time &lt;= #{endDate} </if>" +
            "WHERE e.status = 1 " +
            "GROUP BY e.id, e.name " +
            "ORDER BY totalReduction DESC" +
            "</script>")
    List<EnterpriseRankVO> getEnterpriseGlobalRank(@Param("startDate") String startDate,
                                                   @Param("endDate") String endDate);

    // ==========================================
    // 2. 个人积分排行榜系列 SQL
    // ==========================================

    /**
     * 3. 【大一统排行接口】：获取个人排行榜 (按积分排序)
     * 支持同城、同企业、全平台动态条件查询
     */
    @Select("<script>" +
            "SELECT u.id AS userId, u.real_name AS realName, u.avatar, u.city, " +
            "IFNULL(SUM(cr.reduction), 0) AS totalReduction, " +
            "IFNULL(SUM(cr.points_earned), 0) AS totalPoints " +
            "FROM user u " +
            "LEFT JOIN carbon_record cr ON u.id = cr.user_id AND cr.deleted = 0 " +
            "<if test='startDate != null'> AND cr.record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND cr.record_time &lt;= #{endDate} </if>" +
            "WHERE u.deleted = 0 AND u.status = 1 " +
            "<if test='city != null and city != \"\"'> AND u.city = #{city} </if>" +
            "<if test='orgId != null'> AND u.org_id = #{orgId} </if>" +
            "GROUP BY u.id, u.real_name, u.avatar, u.city " +
            "ORDER BY totalPoints DESC, totalReduction DESC " +
            "</script>")
    List<UserRankVO> getPersonalPointsRank(@Param("city") String city,
                                           @Param("orgId") Long orgId,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate);
}