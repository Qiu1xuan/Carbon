package com.qms.carbon.Vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录请求数据传输对象
 */
@Data
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
}