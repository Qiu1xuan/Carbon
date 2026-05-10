package com.qms.carbon.Service;

import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;
import java.util.List;

/**
 * 排行榜业务 服务接口 (大一统完整版)
 */
public interface RankService {

    // ==========================================
    // 1. 企业级排行榜聚合系列
    // ==========================================

    /**
     * 获取全平台企业总减排量排行榜
     */
    List<EnterpriseRankVO> getEnterpriseGlobalRank(String startDate, String endDate);

    /**
     * 获取企业内部员工的减排量排行榜 (按减排量聚合)
     */
    List<UserRankVO> getEnterpriseInternalRank(Long orgId, String startDate, String endDate);


    // ==========================================
    // 2. 个人积分排行榜系列
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
     * 获取企业内部个人积分排行榜 (按积分聚合)
     */
    List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate);

}