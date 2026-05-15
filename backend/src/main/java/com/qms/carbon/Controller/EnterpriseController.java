package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.Enterprise;
import com.qms.carbon.Entity.User;
import com.qms.carbon.Service.EnterpriseService;
import com.qms.carbon.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 企业与组织架构相关接口
 */
@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private UserService userService;

    /**
     * 1. 获取企业详情（包含企业内部的员工列表）
     * GET /api/enterprise/detail?enterpriseId=1
     */
    @GetMapping("/detail")
    public Result<Enterprise> getEnterpriseDetail(@RequestParam Long enterpriseId) {
        Enterprise enterprise = enterpriseService.getEnterpriseWithUsers(enterpriseId);
        return Result.success(enterprise);
    }

    /**
     * 2. 查询单个用户信息（包含其所属的企业信息）
     * GET /api/enterprise/user/detail?userId=2
     */
    @GetMapping("/user/detail")
    public Result<User> getUserWithEnterprise(@RequestParam Long userId) {
        User user = userService.getUserWithEnterprise(userId);
        return Result.success(user);
    }

    /**
     * 3. 员工绑定/加入企业
     * POST /api/enterprise/bind?userId=2&enterpriseId=1&isAdmin=false
     */
    @PostMapping("/bind")
    public Result<String> bindEnterprise(
            @RequestParam Long userId,
            @RequestParam Long enterpriseId,
            @RequestParam(defaultValue = "false") Boolean isAdmin) {
        try {
            userService.bindEnterprise(userId, enterpriseId, isAdmin);
            return Result.success("绑定成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 4. 员工解绑/退出企业
     * POST /api/enterprise/unbind?userId=2
     */
    @PostMapping("/unbind")
    public Result<String> unbindEnterprise(@RequestParam Long userId) {
        try {
            userService.unbindEnterprise(userId);
            return Result.success("解绑成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}