package com.han.cafe.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FileStorageConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);
    
    @PostConstruct
    public void init() {
        try {
            Path uploadsPath = Paths.get("uploads");
            Path imagesPath = uploadsPath.resolve("images");
            Path avatarPath = uploadsPath.resolve("avatar");
            
            // 创建上传目录
            createDirectoryIfNotExists(uploadsPath);
            createDirectoryIfNotExists(imagesPath);
            createDirectoryIfNotExists(avatarPath);
            
            logger.info("文件上传目录初始化成功");
            logger.info("上传根目录: {}", uploadsPath.toAbsolutePath());
            logger.info("图片目录: {}", imagesPath.toAbsolutePath());
            logger.info("头像目录: {}", avatarPath.toAbsolutePath());
            
        } catch (IOException e) {
            logger.error("创建上传目录失败", e);
            throw new RuntimeException("无法创建上传目录", e);
        }
    }
    
    private void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("创建目录: {}", path);
        }
    }
} 