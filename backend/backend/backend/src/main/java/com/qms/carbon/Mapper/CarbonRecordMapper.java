package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.CarbonRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarbonRecordMapper extends BaseMapper<CarbonRecord> {
    // 把多表联合和统计的逻辑交给 Service 层的 Wrapper 与 Java Stream
}