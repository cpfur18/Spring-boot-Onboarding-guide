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
                                .title("MiniLog Api")
                                .version("1.0")
                                .description("API documentation for the Minilog project"));
    }
}
