package com.asdf.minilog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MinilogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinilogApplication.class, args);
    }
}
