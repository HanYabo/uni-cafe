# Uni-Cafe Spring Boot 项目

基于Spring Boot的咖啡馆后端API服务，实现了用户注册登录、JWT认证等功能。

## 技术栈

- Spring Boot 3.4.x
- Spring Security
- JWT (JSON Web Token)
- MyBatis Plus
- MySQL

## 登录注册功能

本项目实现了基于手机号+密码的登录注册功能，主要包括：

1. 使用Spring Security进行安全控制
2. 基于JWT实现无状态认证
3. 密码采用BCrypt加密存储
4. 支持手机号+密码注册和登录

## API说明

### 注册
- URL: `/api/auth/register`
- 方法: POST
- 请求体:
```json
{
  "mobile": "13800138000",
  "password": "your_password",
  "nickname": "用户昵称",
  "avatarUrl": "头像URL"
}
```
- 响应:
```json
{
  "message": "注册成功",
  "userId": 1
}
```

### 登录
- URL: `/api/auth/login`
- 方法: POST
- 请求体:
```json
{
  "mobile": "13800138000",
  "password": "your_password"
}
```
- 响应:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

## 认证机制

认证成功后，客户端需要在后续请求中添加Authorization请求头：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
``` 