package com.qms.carbon.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.carbon.Entity.Enterprise;
import com.qms.carbon.Entity.User;
import com.qms.carbon.Mapper.EnterpriseMapper;
import com.qms.carbon.Mapper.UserMapper;
import com.qms.carbon.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户表 服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindEnterprise(Long userId, Long enterpriseId, boolean isEnterpriseAdmin) {
        // 1. 校验企业是否存在且状态正常 (status: 1 为正常)
        Enterprise enterprise = enterpriseMapper.selectById(enterpriseId);
        if (enterprise == null || enterprise.getStatus() != 1) {
            // 实际项目中建议抛出自定义的 BusinessException
            throw new RuntimeException("企业不存在或当前状态不可用");
        }

        // 2. 校验用户是否存在
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 更新用户归属及角色
        user.setOrgId(enterpriseId);

        // 如果当前是超级管理员，则不降级/更改其原有权限
        if (!"ADMIN".equals(user.getRole())) {
            if (isEnterpriseAdmin) {
                user.setRole("ENTERPRISE_ADMIN");
            } else {
                user.setRole("USER");
            }
        }

        // 4. 执行更新
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindEnterprise(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            // 只有普通员工或企业管理员解绑时才重置角色，超管跳过角色重置
            String newRole = "ADMIN".equals(user.getRole()) ? "ADMIN" : "USER";

            // 使用 LambdaUpdateWrapper 精确更新字段，允许 orgId 更新为 null
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                    .set(User::getOrgId, null)
                    .set(User::getRole, newRole);

            this.update(updateWrapper);
        }
    }

    @Override
    public User getUserWithEnterprise(Long userId) {
        // 1. 查询基本用户信息
        User user = this.getById(userId);

        // 2. 如果存在归属企业，则装配企业信息
        if (user != null && user.getOrgId() != null) {
            Enterprise enterprise = enterpriseMapper.selectById(user.getOrgId());
            user.setEnterprise(enterprise);
        }

        return user;
    }

    @Override
    public User login(String username, String password) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password) // 按照文档约定，开发阶段使用明文密码
                .eq(User::getDeleted, 0);

        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("该账号已被冻结，请联系管理员");
        }

        // 擦除返回给前端的密码，保证安全性
        user.setPassword(null);
        return user;
    }
}