package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.User;
import com.qms.carbon.Service.UserService;
import com.qms.carbon.Vo.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        // 简单参数校验
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.error("账号和密码不能为空");
        }

        try {
            // 调用 Service 层逻辑
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}