package com.qms.carbon.Controller;

import com.qms.carbon.Common.Result;
import com.qms.carbon.Service.AiService;
import com.qms.carbon.Vo.AiRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/advise")
    public Result<String> getAdvice(@RequestBody AiRequestDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return Result.error("请输入您的问题或数据");
        }
        String aiResponse = aiService.getGreenAdvice(dto.getContent());
        return Result.success(aiResponse);
    }
}