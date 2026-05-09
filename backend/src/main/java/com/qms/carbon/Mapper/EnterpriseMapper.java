package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业表 Mapper 接口
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {
}