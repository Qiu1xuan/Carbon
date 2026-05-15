package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.MerkleDailySummary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日默克尔根摘要 Mapper 接口
 */
@Mapper
public interface MerkleDailySummaryMapper extends BaseMapper<MerkleDailySummary> {
    // 继承了 BaseMapper，MyBatis-Plus 会自动帮我们实现增删改查，这里保持为空即可
}