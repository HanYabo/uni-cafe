package com.han.cafe.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.han.cafe.utils.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService adminUserDetailsService;

    public AdminJwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService adminUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.adminUserDetailsService = adminUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 只处理/api/admin/开头的请求
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/api/admin/") || requestURI.equals("/api/admin/login")) {
            chain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        Claims claims = null;

        if (authorizationHeader != null) {
            jwt = authorizationHeader;
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwt);
                claims = jwtTokenUtil.getClaimsFromToken(jwt);
            } catch (Exception e) {
                // Token无效，继续处理请求
                logger.error("JWT解析失败", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.adminUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                // 优先使用 UserDetails 中的权限（从数据库中获取的最新权限）
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
} 