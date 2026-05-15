package com.qms.carbon.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qms.carbon.Common.Result;
import com.qms.carbon.Entity.CarbonFactorLog;
import com.qms.carbon.Mapper.CarbonFactorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/factor")
public class CarbonFactorLogController {

    @Autowired
    private CarbonFactorLogMapper factorLogMapper;

    /**
     * 获取所有当前有效的减排行为因子
     * 作用：前端用户点击“上报”时，下拉框显示的选项（如：步行、骑行、节电）
     */
    @GetMapping("/factors")
    public Result<List<CarbonFactorLog>> getAvailableFactors() {
        // 只查询当前生效的版本 (isCurrent = 1)
        List<CarbonFactorLog> factors = factorLogMapper.selectList(
                new LambdaQueryWrapper<CarbonFactorLog>()
                        .eq(CarbonFactorLog::getIsCurrent, 1)
                        .orderByAsc(CarbonFactorLog::getId)
        );
        return Result.success(factors);
    }
}