package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 碳积分记录表 Entity
 */
@Data
@Accessors(chain = true)
@TableName("points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer pointsChange;

    private Integer pointsBefore;

    private Integer pointsAfter;

    /**
     * EARN,REDEEM,EXPIRE
     */
    private String type;

    private Long sourceId;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}