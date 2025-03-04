package com.han.cafe.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterVO {
    
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String nickname;
    
    private String avatarUrl;
} 