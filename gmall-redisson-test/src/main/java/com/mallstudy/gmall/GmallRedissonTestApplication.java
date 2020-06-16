package com.mallstudy.gmall;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
public class GmallRedissonTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallRedissonTestApplication.class, args);
    }

}
