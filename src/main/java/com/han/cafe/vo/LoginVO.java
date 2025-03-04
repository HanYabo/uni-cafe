package com.han.cafe.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginVO {
    
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    
    @NotBlank(message = "密码不能为空")
    private String password;
} 