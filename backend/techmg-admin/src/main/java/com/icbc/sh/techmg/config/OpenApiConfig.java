package com.icbc.sh.techmg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("techmg 技术管理平台")
                        .description("技术管理平台 — 上海研发基地")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ICBC Shanghai R&D Center")));
    }
}
