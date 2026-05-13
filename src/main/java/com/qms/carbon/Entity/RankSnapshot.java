package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排行榜快照 Entity
 */
@Data
@Accessors(chain = true)
@TableName("rank_snapshot")
public class RankSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDate rankDate;

    /**
     * PERSONAL,ENTERPRISE,CITY
     */
    private String rankType;

    private Long userId;

    private Long enterpriseId;

    private String city;

    private Integer rankNum;

    private Long carbonTotal;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}