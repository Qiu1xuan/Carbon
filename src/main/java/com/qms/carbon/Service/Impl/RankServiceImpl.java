package com.qms.carbon.Service.Impl;

import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Service.RankService;
import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 排行榜业务 服务实现类
 */
@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private CarbonRecordMapper carbonRecordMapper;

    // ==========================================
    // 1. 企业级排行榜实现
    // ==========================================

    @Override
    public List<EnterpriseRankVO> getEnterpriseGlobalRank(String startDate, String endDate) {
        List<EnterpriseRankVO> rankList = carbonRecordMapper.getEnterpriseGlobalRank(startDate, endDate);
        int currentRank = 1;
        for (int i = 0; i < rankList.size(); i++) {
            EnterpriseRankVO current = rankList.get(i);
            if (i > 0 && current.getTotalReduction().equals(rankList.get(i - 1).getTotalReduction())) {
                current.setRankNum(rankList.get(i - 1).getRankNum());
            } else {
                current.setRankNum(currentRank);
            }
            currentRank++;
        }
        return rankList;
    }

    @Override
    public List<UserRankVO> getEnterpriseInternalRank(Long orgId, String startDate, String endDate) {
        List<UserRankVO> rankList = carbonRecordMapper.getEnterpriseInternalRank(orgId, startDate, endDate);
        return calculatePersonalRank(rankList, false);
    }


    // ==========================================
    // 2. 个人积分排行榜实现
    // ==========================================

    @Override
    public List<UserRankVO> getGlobalPersonalRank(String startDate, String endDate) {
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(null, null, startDate, endDate);
        return calculatePersonalRank(rankList, true);
    }

    @Override
    public List<UserRankVO> getCityPersonalRank(String city, String startDate, String endDate) {
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(city, null, startDate, endDate);
        return calculatePersonalRank(rankList, true);
    }

    @Override
    public List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate) {
        List<UserRankVO> rankList = carbonRecordMapper.getPersonalPointsRank(null, orgId, startDate, endDate);
        return calculatePersonalRank(rankList, true);
    }


    // ==========================================
    // 3. 通用并列排名计算逻辑
    // ==========================================

    /**
     * 计算个人名次 (支持并列排名)
     * @param rankList 列表数据
     * @param isPointsRank 是否是积分排行（如果是减排榜则判断减排量，如果是积分榜则判断积分）
     */
    private List<UserRankVO> calculatePersonalRank(List<UserRankVO> rankList, boolean isPointsRank) {
        if (rankList == null || rankList.isEmpty()) {
            return rankList;
        }
        int currentRank = 1;
        for (int i = 0; i < rankList.size(); i++) {
            UserRankVO current = rankList.get(i);
            if (i > 0) {
                UserRankVO prev = rankList.get(i - 1);
                boolean isTie = isPointsRank ?
                        current.getTotalPoints().equals(prev.getTotalPoints()) :
                        current.getTotalReduction().equals(prev.getTotalReduction());
                if (isTie) {
                    current.setRankNum(prev.getRankNum());
                } else {
                    current.setRankNum(currentRank);
                }
            } else {
                current.setRankNum(currentRank);
            }
            currentRank++;
        }
        return rankList;
    }
}