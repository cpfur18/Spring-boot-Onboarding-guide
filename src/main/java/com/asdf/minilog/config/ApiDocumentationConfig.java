package com.asdf.minilog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfig {

    @Bean
    public OpenAPI apiDocumentation() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("MiniLog List Api")
                                .version("2.0")
                                .description("Spring Boot3를 이용한 MiniLog LIST API 문서"));
    }
}
