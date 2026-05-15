package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 碳减排因子日志 Entity
 */
@Data
@Accessors(chain = true)
@TableName("carbon_factor_log")
public class CarbonFactorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * WALK,BIKE,SAVE_ELECTRICITY...
     */
    private String activityType;

    private String activityName;

    /**
     * 减排因子
     */
    private BigDecimal factor;

    private String unit;

    private String unitDesc;

    private String version;

    private String standardSource;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /**
     * 是否当前版本 (0:否, 1:是)
     */
    @TableField("is_current")
    private Integer isCurrent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}