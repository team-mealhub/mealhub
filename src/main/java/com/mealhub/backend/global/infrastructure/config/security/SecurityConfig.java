package com.mealhub.backend.global.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtAuthorizationFilter;
import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtUtil;
import com.mealhub.backend.global.infrastructure.config.security.jwt.handler.JwtAccessDeniedHandler;
import com.mealhub.backend.global.infrastructure.config.security.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
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
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // JWT 방식 사용을 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 인증·인가 예외 핸들러 등록
        http.exceptionHandling((exceptions) ->
                exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/api-docs.html")
                        .permitAll()
                        .requestMatchers("/v1/auth/**").permitAll()
                        // Restaurant
                        .requestMatchers(HttpMethod.POST, "/v1/restaurant/**")
                        .hasAnyRole("OWNER", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/restaurant/**")
                        .hasAnyRole("OWNER", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/restaurant/**")
                        .hasAnyRole("OWNER", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/restaurant/**").permitAll()
                        // Restaurant Category
                        .requestMatchers(HttpMethod.POST, "/v1/restaurant/category")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/restaurant/category/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/restaurant/category/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/restaurant/category").permitAll()
                        // Ai
                        .requestMatchers(HttpMethod.POST, "/v1/ai/generate-description")
                        .hasAnyRole("OWNER", "MANAGER")
                        .anyRequest().authenticated()
        );

        // 필터 등록
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
