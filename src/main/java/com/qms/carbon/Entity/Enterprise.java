package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 企业表 Entity
 */
@Data
@Accessors(chain = true)
@TableName("enterprise")
public class Enterprise implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 统一社会信用代码
     */
    private String code;

    private String city;

    private String industry;

    /**
     * 碳配额（克）
     */
    private Long carbonQuota;

    /**
     * 已使用配额
     */
    private Long carbonUsed;

    private String contactPerson;

    private String contactPhone;

    /**
     * 状态（1:正常）
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}