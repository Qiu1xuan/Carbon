package com.qms.carbon.Task;

import com.qms.carbon.Service.VerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MerkleTask {

    private static final Logger log = LoggerFactory.getLogger(MerkleTask.class);

    @Autowired
    private VerifyService verifyService;

    /**
     * 每天 23:59:59 自动执行当天的减排记录哈希打包上链
     * cron 表达式从左到右：秒 分 时 日 月 星期
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void autoGenerateMerkleRoot() {
        LocalDate today = LocalDate.now();
        log.info("====== 开始执行每日默克尔树存证任务，日期：{} ======", today);
        try {
            verifyService.generateDailyMerkleRoot(today);
            log.info("====== 今日数据存证成功！======");
        } catch (Exception e) {
            log.error("====== 今日暂无数据或存证失败：{} ======", e.getMessage());
        }
    }
}