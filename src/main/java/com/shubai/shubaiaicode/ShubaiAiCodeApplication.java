package com.shubai.shubaiaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shubai.shubaiaicode.mapper")
public class ShubaiAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShubaiAiCodeApplication.class, args);
    }

}
