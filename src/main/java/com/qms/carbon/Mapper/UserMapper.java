package com.qms.carbon.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.carbon.Entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 已提供基础的 CRUD 方法，如有复杂 SQL 需求可在此处定义
}