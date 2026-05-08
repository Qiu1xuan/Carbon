package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表 Entity
 */
@Data
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（明文，开发用）
     */
    private String password;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    /**
     * ADMIN / ENTERPRISE_ADMIN / USER
     */
    private String role;

    /**
     * 所属企业ID
     */
    private Long orgId;

    private String city;

    /**
     * 总减排量（克）
     */
    private Long carbonTotal;

    /**
     * 碳积分
     */
    private Integer points;

    /**
     * 状态（1:正常）
     */
    private Integer status;

    private LocalDateTime lastLoginTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（0:未删除，1:已删除）
     */
    @TableLogic
    private Integer deleted;
}