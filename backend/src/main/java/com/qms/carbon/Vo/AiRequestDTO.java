package com.qms.carbon.Vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class AiRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    // 前端传过来的用户的提问/数据
    private String content;
}