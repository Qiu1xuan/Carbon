package com.qms.carbon.Service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qms.carbon.Service.AiService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
public class AiServiceImpl implements AiService {

    // 这里以目前火爆且便宜的 DeepSeek 为例，兼容 OpenAI 格式。
    // 如果你要用真实的，去 deepseek 官网申请个 key 替换下面这个字符串即可。
    private static final String API_KEY = "sk-89e0709b3fd646b5ba8a0cd1ab4365a6";
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    @Override
    public String getGreenAdvice(String userContent) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // 构造请求体 (标准 OpenAI 格式)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");

            // 系统预设 prompt，让 AI 扮演低碳专家
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的碳普惠与低碳生活智能顾问。请根据用户提供的行为数据，给出2-3条简短、温暖、具体的改进建议。");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userContent);

            requestBody.put("messages", java.util.Arrays.asList(systemMessage, userMessage));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发起请求
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            // 解析 JSON 返回值
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 顾问正在休息中，请稍后再试。错误信息：" + e.getMessage();
        }
    }

}