package com.mallstudy.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

//扫描mapper时要使用通用mapper的 tk……..MapperScan
@SpringBootApplication
@MapperScan(basePackages = "com.mallstudy.gmall.user.mapper")
public class GmallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserApplication.class, args);
    }

}
