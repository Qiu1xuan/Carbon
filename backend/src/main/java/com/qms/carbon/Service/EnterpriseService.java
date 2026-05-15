package com.qms.carbon.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.carbon.Entity.Enterprise;

/**
 * 企业表 服务类接口
 */
public interface EnterpriseService extends IService<Enterprise> {

    /**
     * 获取企业详情及其关联的员工/用户列表
     *
     * @param enterpriseId 企业ID
     * @return 带有用户列表的企业对象
     */
    Enterprise getEnterpriseWithUsers(Long enterpriseId);
}