package com.qms.carbon.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qms.carbon.Entity.Enterprise;
import com.qms.carbon.Entity.User;
import com.qms.carbon.Entity.CarbonRecord;
import com.qms.carbon.Mapper.CarbonRecordMapper;
import com.qms.carbon.Mapper.EnterpriseMapper;
import com.qms.carbon.Mapper.UserMapper;
import com.qms.carbon.Service.RankService;
import com.qms.carbon.Vo.EnterpriseRankVO;
import com.qms.carbon.Vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private CarbonRecordMapper carbonRecordMapper;
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private UserMapper userMapper;

    // ==========================================
    // 1. 企业级排行榜实现 (纯 MP 调用)
    // ==========================================
    @Override
    public List<EnterpriseRankVO> getEnterpriseGlobalRank(String startDate, String endDate) {
        // 1. 查询所有正常营业的企业
        List<Enterprise> enterprises = enterpriseMapper.selectList(
                new LambdaQueryWrapper<Enterprise>().eq(Enterprise::getStatus, 1)
        );
        if (enterprises.isEmpty()) return Collections.emptyList();

        List<Long> entIds = enterprises.stream().map(Enterprise::getId).collect(Collectors.toList());

        // 2. 查询这些企业下的所有正常员工
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getDeleted, 0).in(User::getOrgId, entIds)
        );

        // 映射：userId -> orgId (方便后续找归属)
        Map<Long, Long> userToOrgMap = users.stream().collect(Collectors.toMap(User::getId, User::getOrgId));

        // 初始化企业减排量 Map 为 0
        Map<Long, Long> orgReductionMap = enterprises.stream().collect(Collectors.toMap(Enterprise::getId, e -> 0L));

        // 3. 查询减排记录表，利用 MP 的 selectMaps 进行 SUM 和 GroupBy
        if (!users.isEmpty()) {
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            QueryWrapper<CarbonRecord> recordQw = new QueryWrapper<>();
            recordQw.select("user_id AS userId", "IFNULL(SUM(reduction), 0) AS totalReduction")
                    .eq("deleted", 0)
                    .in("user_id", userIds);
            if (startDate != null && !startDate.isEmpty()) recordQw.ge("record_time", startDate);
            if (endDate != null && !endDate.isEmpty()) recordQw.le("record_time", endDate);
            recordQw.groupBy("user_id");

            // 获取分组统计结果 [{userId=1, totalReduction=1500}, ...]
            List<Map<String, Object>> mapList = carbonRecordMapper.selectMaps(recordQw);

            // 4. 将员工个人的减排量汇总到对应企业的池子里
            for (Map<String, Object> map : mapList) {
                Long uid = ((Number) map.get("userId")).longValue();
                Long reduction = ((Number) map.get("totalReduction")).longValue();
                Long orgId = userToOrgMap.get(uid);
                if (orgId != null) {
                    orgReductionMap.put(orgId, orgReductionMap.get(orgId) + reduction);
                }
            }
        }

        // 5. 组装 VO 列表并进行排序 (减排倒序，ID正序)
        List<EnterpriseRankVO> rankList = new ArrayList<>();
        for (Enterprise e : enterprises) {
            rankList.add(new EnterpriseRankVO()
                    .setEnterpriseId(e.getId())
                    .setEnterpriseName(e.getName())
                    .setTotalReduction(orgReductionMap.get(e.getId())));
        }

        rankList.sort((a, b) -> {
            if (!b.getTotalReduction().equals(a.getTotalReduction())) {
                return b.getTotalReduction().compareTo(a.getTotalReduction());
            }
            return a.getEnterpriseId().compareTo(b.getEnterpriseId());
        });

        // 6. 分配排名 (同分并列)
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


    // ==========================================
    // 2. 个人级积分排行榜实现 (大一统合并)
    // ==========================================

    @Override
    public List<UserRankVO> getGlobalPersonalRank(String startDate, String endDate) {
        return buildPersonalRank(null, null, startDate, endDate);
    }

    @Override
    public List<UserRankVO> getCityPersonalRank(String city, String startDate, String endDate) {
        return buildPersonalRank(city, null, startDate, endDate);
    }

    @Override
    public List<UserRankVO> getEnterprisePersonalRank(Long orgId, String startDate, String endDate) {
        return buildPersonalRank(null, orgId, startDate, endDate);
    }

    /**
     * 核心实现：利用 MP Wrapper 动态组装个人排行数据
     */
    private List<UserRankVO> buildPersonalRank(String city, Long orgId, String startDate, String endDate) {
        // 1. 动态查询目标用户群
        LambdaQueryWrapper<User> userQw = new LambdaQueryWrapper<User>().eq(User::getDeleted, 0).eq(User::getStatus, 1);
        if (city != null && !city.isEmpty()) userQw.eq(User::getCity, city);
        if (orgId != null) userQw.eq(User::getOrgId, orgId);
        List<User> users = userMapper.selectList(userQw);
        if (users.isEmpty()) return Collections.emptyList();

        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        // 2. 查询选中用户的碳减排聚合数据
        QueryWrapper<CarbonRecord> recordQw = new QueryWrapper<>();
        recordQw.select("user_id AS userId",
                        "IFNULL(SUM(reduction), 0) AS totalReduction",
                        "IFNULL(SUM(points_earned), 0) AS totalPoints")
                .eq("deleted", 0)
                .in("user_id", userIds);
        if (startDate != null && !startDate.isEmpty()) recordQw.ge("record_time", startDate);
        if (endDate != null && !endDate.isEmpty()) recordQw.le("record_time", endDate);
        recordQw.groupBy("user_id");

        List<Map<String, Object>> mapList = carbonRecordMapper.selectMaps(recordQw);

        // 将查出的聚合结果转为 userId -> Map
        Map<Long, Map<String, Object>> aggMap = mapList.stream()
                .collect(Collectors.toMap(m -> ((Number) m.get("userId")).longValue(), m -> m));

        // 3. 组装 VO (就算用户没有产生记录，这里也会赋 0 分，实现类似 LEFT JOIN 的效果)
        List<UserRankVO> rankList = new ArrayList<>();
        for (User user : users) {
            UserRankVO vo = new UserRankVO()
                    .setUserId(user.getId())
                    .setRealName(user.getRealName())
                    .setAvatar(user.getAvatar())
                    .setCity(user.getCity());

            Map<String, Object> data = aggMap.get(user.getId());
            if (data != null) {
                vo.setTotalReduction(((Number) data.get("totalReduction")).longValue());
                vo.setTotalPoints(((Number) data.get("totalPoints")).intValue());
            } else {
                vo.setTotalReduction(0L);
                vo.setTotalPoints(0);
            }
            rankList.add(vo);
        }

        // 4. Java 内存排序：积分倒序，同分则按减排倒序
        rankList.sort((a, b) -> {
            if (!b.getTotalPoints().equals(a.getTotalPoints())) {
                return b.getTotalPoints().compareTo(a.getTotalPoints());
            }
            return b.getTotalReduction().compareTo(a.getTotalReduction());
        });

        // 5. 分配并列排名
        int currentRank = 1;
        for (int i = 0; i < rankList.size(); i++) {
            UserRankVO current = rankList.get(i);
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