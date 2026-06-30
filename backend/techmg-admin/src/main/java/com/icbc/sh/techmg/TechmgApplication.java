package com.icbc.sh.techmg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@MapperScan({"com.icbc.sh.techmg.system.mapper", "com.icbc.sh.techmg.business.mapper"})
public class TechmgApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechmgApplication.class, args);
    }
}
