package com.qms.carbon.Service.Impl;

import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Service.RankService;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private CarbonRecordMapper carbonRecordMapper;

    // --- 1. 全平台排行 ---
    @Override
    public List<UserRankVO> getGlobalPersonalRank(String startDate, String endDate) {
        // city 和 orgId 传 null，就是查全平台
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(null, null, startDate, endDate);
        return calculateRank(rankList);
    }

    // --- 2. 同城排行 ---
    @Override
    public List<UserRankVO> getCityPersonalRank(String city, String startDate, String endDate) {
        // 传入 city，orgId 传 null
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(city, null, startDate, endDate);
        return calculateRank(rankList);
    }

    // --- 3. 同企业排行 ---
    @Override
    public List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate) {
        // 传入 orgId，city 传 null
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(null, orgId, startDate, endDate);
        return calculateRank(rankList);
    }

    /**
     * 私有公共方法：计算并排位名次（支持积分并列）
     */
    private List<UserRankVO> calculateRank(List<UserRankVO> rankList) {
        if (rankList == null || rankList.isEmpty()) {
            return rankList;
        }

        int currentRank = 1;
        for (int i = 0; i < rankList.size(); i++) {
            UserRankVO current = rankList.get(i);

            // 如果不是第一名，且积分和上一名完全一样，则名次并列
            if (i > 0 && current.getTotalPoints().equals(rankList.get(i - 1).getTotalPoints())) {
                current.setRankNum(rankList.get(i - 1).getRankNum());
            } else {
                current.setRankNum(currentRank);
            }
            currentRank++;
        }
        return rankList;
    }
}