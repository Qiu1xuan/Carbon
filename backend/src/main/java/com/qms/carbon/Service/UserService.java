package com.qms.carbon.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.carbon.Entity.User;

/**
 * 用户表 服务类接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户绑定企业
     *
     * @param userId            用户ID
     * @param enterpriseId      企业ID
     * @param isEnterpriseAdmin 是否设置为企业管理员
     */
    void bindEnterprise(Long userId, Long enterpriseId, boolean isEnterpriseAdmin);

    /**
     * 用户解绑企业
     *
     * @param userId 用户ID
     */
    void unbindEnterprise(Long userId);

    /**
     * 获取用户详细信息（包含所关联的企业数据）
     *
     * @param userId 用户ID
     * @return 带有企业信息的用户对象
     */
    User getUserWithEnterprise(Long userId);
}