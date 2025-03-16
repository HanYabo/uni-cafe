package com.han.cafe.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.han.cafe.filter.AdminJwtAuthenticationFilter;
import com.han.cafe.filter.JwtAuthenticationFilter;
import com.han.cafe.utils.JwtTokenUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserDetailsService adminUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public SecurityConfig(
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, 
            @Lazy @Qualifier("adminUserDetailsService") UserDetailsService adminUserDetailsService, 
            JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 创建JWT过滤器
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService);
        AdminJwtAuthenticationFilter adminJwtAuthenticationFilter = new AdminJwtAuthenticationFilter(jwtTokenUtil, adminUserDetailsService);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                // 静态资源和上传文件访问放在最前面
                .requestMatchers(
                    "/static/**", 
                    "/uploads/**", 
                    "/images/**",
                    "/*.ico",
                    "/*.html",
                    "/*.js",
                    "/*.css"
                ).permitAll()
                // API接口访问
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/file/upload", 
                    "/api/categories/**", 
                    "/api/orders/**",
                    "/api/coupons/**",
                    "/api/admin/login",
                        "api/cache/**",
                    "/api/products/**"
                ).permitAll()
                // 管理员接口需要ADMIN角色
                .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                // 特权接口需要超级管理员角色
                .requestMatchers("/api/admin/system/**").hasAuthority("ROLE_SUPER_ADMIN")
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(adminJwtAuthenticationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 