package com.qms.carbon.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日默克尔根摘要 Entity
 */
@Data
@Accessors(chain = true)
@TableName("merkle_daily_summary")
public class MerkleDailySummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDate recordDate;

    private String merkleRoot;

    private Integer recordCount;

    private Long totalReduction;

    private String blockHash;

    private String txId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}