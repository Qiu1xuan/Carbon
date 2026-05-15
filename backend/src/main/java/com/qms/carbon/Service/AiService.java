package com.qms.carbon.Service;

public interface AiService {
    /**
     * 获取 AI 低碳建议
     */
    String getGreenAdvice(String userContent);
}