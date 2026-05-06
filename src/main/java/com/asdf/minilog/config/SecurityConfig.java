package com.asdf.minilog.config;

import com.asdf.minilog.security.JwtAuthenticationEntryPoint;
import com.asdf.minilog.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// 스프링 시큐리티 기본 설정 적용
@EnableWebSecurity
@EnableMethodSecurity(proxyTargetClass = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API는 세션 기반이 아니고 CSRF 토큰을 사용하는 폼 요청 없기 때문에 노출 가능성 낮음
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (requests) ->
                                requests
                                        .requestMatchers("/api/v2/auth/login", "/swagger-ui/**",
                                                "v3/api-docs/**")
                                        .permitAll()
                                        // 사용자 생성, 조회는 인증 없이 가능
                                        .requestMatchers(HttpMethod.POST, "/api/v2/user")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v2/user/{userId}")
                                        .permitAll()
                                        // 사용자 삭제는 ADMIN 권한 필요
                                        .requestMatchers(HttpMethod.DELETE, "/api/v2/user/{userId}")
                                        .hasRole("ADMIN")
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        exceptionHandling ->
                                exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement( // JWT는 세션을 사용하지 않기에 SessionCreationPolicy.STATELESS로 세션 생성이나 유지 X
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 기존 인증 필터보다 JWT 필터를 먼저 수행하도록 하도록 설정
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }



}
