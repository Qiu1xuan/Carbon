package com.qms.carbon.Service;

import com.qms.carbon.Vo.UserRankVO;
import java.util.List;

public interface RankService {

    // ------ 个人积分排行系列 ------

    /**
     * 1. 获取全平台个人积分排行榜
     */
    List<UserRankVO> getGlobalPersonalRank(String startDate, String endDate);

    /**
     * 2. 获取同城个人积分排行榜
     */
    List<UserRankVO> getCityPersonalRank(String city, String startDate, String endDate);

    /**
     * 3. 获取企业内部个人积分排行榜
     */
    List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate);
}