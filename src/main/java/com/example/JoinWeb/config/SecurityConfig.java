package com.example.JoinWeb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// Spring Security를 활성화한다. 스프링 3.X부터는 생략가능한듯?
@EnableWebSecurity
public class SecurityConfig {
    // HttpSecurity를 받아 인증/인가, CORS, CSRF 등 웹 보안 정책을 코드로 설정한다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // 오류 발생으로 개발단계에서 전체 허용, 추후 조정 가능
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/register").permitAll()//해당 경로는 누구나 접근 가능하도록
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()//DB도 접근 가능하도록
                        .anyRequest().permitAll() // 모든 요청 인증 없이 허용
                )
                .headers(headers
                        -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .cors(cors -> {}); //다른 출처에서 API를 호출하도록 한다.
        return http.build(); //위의 설정값을 토대로 SecurityFilterChain 객체를 생성해서 빈으로 등록한다.
    }

    // 비밀번호 암호화(해시처리인듯) 시키기.
    // @AutoWired로 바로 쓸 수 있음
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
