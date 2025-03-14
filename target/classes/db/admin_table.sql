-- 创建管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    `admin_id` INT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-正常',
    `role` TINYINT NOT NULL DEFAULT 2 COMMENT '1-超级管理员 2-普通管理员',
    `last_login` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`admin_id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- 初始化超级管理员账号 (密码: admin123)
-- 注意: 密码使用了BCrypt加密, 实际密码是admin123
INSERT INTO `admin` (`username`, `password`, `name`, `role`)
VALUES ('admin', '$2a$10$mMqHNKCTgL7u3nY1.wPgAuhYwI.LUEXyazXoWGcNOrFgL5PSqMYmy', '超级管理员', 1); 