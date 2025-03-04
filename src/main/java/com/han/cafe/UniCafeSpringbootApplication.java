package com.han.cafe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.han.cafe.mapper")
public class UniCafeSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCafeSpringbootApplication.class, args);
    }

}
