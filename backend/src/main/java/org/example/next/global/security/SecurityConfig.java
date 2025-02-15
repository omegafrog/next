package org.example.next.global.security;

import org.example.next.domain.member.member.service.MemberService;
import org.example.next.global.Rq;
import org.example.next.global.dto.RsData;
import org.example.next.util.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberService memberService, Rq rq) throws Exception {
        http
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/*/posts/{id:\\d+}", "/api/*/posts", "/api/*/posts/{id:\\d+}", "/api/*/posts/{id:\\d+}/comments")
                                .permitAll()
                                .requestMatchers("/h2-console/**")
                                .permitAll()
                                .requestMatchers("/api/*/members/login", "/api/*/members/join", "/api/*/members/logout")
                                .permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers("/api/v1/posts/statistics")
                                .hasAuthority("ADMIN_ACT")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new CustomAuthenticationFilter(rq, memberService), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                        (request, response, ex) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    Util.Json.toJson(
                                                            new RsData<>(
                                                                    "401-1",
                                                                    "인증이 필요합니다."
                                                            )
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=UTF-8");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    Util.Json.toJson(
                                                            new RsData("403-1", "접근 권한이 없습니다.")
                                                    )
                                            );
                                        }
                                ));
        return http.build();
    }
}
