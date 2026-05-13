package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 减排记录表 Entity
 */
@Data
@Accessors(chain = true)
@TableName("carbon_record")
public class CarbonRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String activityType;

    private BigDecimal activityValue;

    /**
     * 减排量（克）
     */
    private Long reduction;

    private Integer pointsEarned;

    private LocalDateTime recordTime;

    private String location;

    private String factorVersion;

    private BigDecimal factorValue;

    private String merkleHash;

    private String merkleRoot;

    private Integer verifyStatus;

    private String source;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}