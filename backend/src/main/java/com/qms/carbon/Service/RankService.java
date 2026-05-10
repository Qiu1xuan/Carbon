package com.qms.carbon.Service;

import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;

import java.util.List;

/**
 * 排行榜业务 服务接口
 */
public interface RankService {

    // ==========================================
    // 1. 企业级排行榜
    // ==========================================

    /**
     * 获取全平台企业减排量排行榜
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 企业排行榜列表
     */
    List<EnterpriseRankVO> getEnterpriseGlobalRank(String startDate, String endDate);


    // ==========================================
    // 2. 个人级积分排行榜
    // ==========================================

    /**
     * 获取全平台个人积分排行榜
     */
    List<UserRankVO> getGlobalPersonalRank(String startDate, String endDate);

    /**
     * 获取同城个人积分排行榜
     */
    List<UserRankVO> getCityPersonalRank(String city, String startDate, String endDate);

    /**
     * 获取同企业内部个人积分排行榜 (也就是之前的 getEnterpriseInternalRank)
     */
    List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate);
}