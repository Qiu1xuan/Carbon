package com.qms.carbon.Vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 企业级减排量汇总排行榜 VO
 */
@Data
@Accessors(chain = true)
public class EnterpriseRankVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rankNum;       // 排名名次
    private Long enterpriseId;     // 企业ID
    private String enterpriseName; // 企业名称
    private Long totalReduction;   // 企业总减排量(克)
}