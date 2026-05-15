package com.qms.carbon.Vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 企业内部员工减排量排行榜 VO
 */
@Data
@Accessors(chain = true)
public class UserRankVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rankNum;       // 排名名次
    private Long userId;           // 用户ID
    private String realName;       // 真实姓名
    private String avatar;         // 头像
    private String city;           // 所在城市 (同城排行展示用)
    private Long totalReduction;   // 累计减排量(克)
    private Integer totalPoints;   // 累计积分 (本次新增)
}