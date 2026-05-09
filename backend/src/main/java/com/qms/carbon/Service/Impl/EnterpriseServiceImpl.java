package com.qms.carbon.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.carbon.Entity.Enterprise;
import com.qms.carbon.Entity.User;
import com.qms.carbon.Mapper.EnterpriseMapper;
import com.qms.carbon.Mapper.UserMapper;
import com.qms.carbon.Service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 企业表 服务实现类
 */
@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements EnterpriseService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Enterprise getEnterpriseWithUsers(Long enterpriseId) {
        // 1. 获取企业基本信息
        Enterprise enterprise = this.getById(enterpriseId);

        if (enterprise != null) {
            // 2. 构造查询条件：获取 org_id 为该企业 ID 的所有用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getOrgId, enterpriseId)
                    .eq(User::getDeleted, 0); // 排除已逻辑删除的用户

            List<User> users = userMapper.selectList(queryWrapper);

            // 3. 安全处理：擦除返回列表中的员工密码信息
            users.forEach(u -> u.setPassword(null));

            // 4. 将用户列表装配到企业实体中
            enterprise.setUserList(users);
        }

        return enterprise;
    }
}