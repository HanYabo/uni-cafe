package com.han.cafe.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatLoginVO {
    
    @NotBlank(message = "code不能为空")
    private String code;

    private String nickname;
    
    private String avatarUrl;
} 