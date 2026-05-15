package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.CarbonRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface CarbonRecordMapper extends BaseMapper<CarbonRecord> {
    // 统计个人最近7天的每日减排量总和（克）
    @Select("SELECT DATE(record_time) as date, SUM(reduction) as total " +
            "FROM carbon_record " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "GROUP BY DATE(record_time) ORDER BY date ASC LIMIT 7")
    List<Map<String, Object>> getWeeklyStats(Long userId);
}