package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 图书馆座位预约系统启动类
 */
@SpringBootApplication
@MapperScan("com.library.mapper")
@EnableScheduling
public class LibraryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("图书馆座位预约系统编号2026-启动成功！");
        System.out.println("访问地址：http://localhost:8080/api");
    }
}