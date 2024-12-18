package com.example.demo.config;

import com.example.demo.jwt.CustomLogoutFilter;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;
import com.example.demo.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

//스프링 시큐리티의 인가 및 설정을 담당하는 클래스
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
        private final AuthenticationConfiguration authenticationConfiguration;

        private final JWTUtil jwtUtil;

        private final RefreshRepository refreshRepository;

        public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
                        RefreshRepository refreshRepository) {

                this.authenticationConfiguration = authenticationConfiguration;
                this.jwtUtil = jwtUtil;
                this.refreshRepository = refreshRepository;
        }

        // AuthenticationManager Bean 등록
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();
        }

        // 암호화를 위해 사용
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {

                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                // CORS 설정
                http
                                .cors((cors) -> cors
                                                .configurationSource(new CorsConfigurationSource() {

                                                        @Override
                                                        public CorsConfiguration getCorsConfiguration(
                                                                        HttpServletRequest request) {

                                                                CorsConfiguration configuration = new CorsConfiguration();

                                                                configuration.setAllowedOrigins(Arrays.asList(
                                                                                "http://localhost:3000",
                                                                                "http://fitpin-web-back.kro.kr",
                                                                                "https://fit-pin.github.io",
                                                                                "http://localhost",
                                                                                "http://korseok.kro.kr"));
                                                                configuration.setAllowedMethods(Arrays.asList("GET",
                                                                                "POST", "PUT", "DELETE", "OPTIONS"));
                                                                configuration.setAllowedHeaders(
                                                                                Collections.singletonList("*"));
                                                                configuration.setAllowCredentials(true);
                                                                configuration.setExposedHeaders(Arrays.asList(
                                                                                "Authorization", "Content-Type"));
                                                                configuration.setMaxAge(3600L);

                                                                return configuration;
                                                        }
                                                }));

                // csrf disable
                http
                                .csrf((auth) -> auth.disable());

                // From 로그인 방식 disable
                http
                                .formLogin((auth) -> auth.disable());

                // http basic 인증 방식 disable
                http
                                .httpBasic((auth) -> auth.disable());

                // 경로별 인가 작업
                http
                                .authorizeHttpRequests((auth) -> auth
                                                .requestMatchers("/login", "/", "/join", "/check-username", "/users/**" , "/logout")
                                                .permitAll()
                                                .requestMatchers("/token", "/inquiry/**", "list", "/inquiryImg/**",
                                                                "/getauction/**")
                                                .permitAll()
                                                .requestMatchers("/admin").hasRole("ADMIN")
                                                .requestMatchers("/reissue").permitAll()
                                                // 웹 소켓 안되는게 이거 문제였누...
                                                .requestMatchers("/sock").permitAll()
                                                .requestMatchers("/action").permitAll()
                                                .requestMatchers("/recv").permitAll()
                                                .requestMatchers("/weborder").permitAll()
                                                .requestMatchers("/auction_listener/**").permitAll()
                                                .anyRequest().authenticated());

                // JWTFilter 등록
                http
                                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

                // 필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에
                // authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
                // AuthenticationManager()와 JWTUtil 인수 전달
                http
                                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),
                                                jwtUtil, refreshRepository),
                                                UsernamePasswordAuthenticationFilter.class);

                // LogoutFilter 등록
                http
                                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository),
                                                LogoutFilter.class);

                // 세션 설정
                http
                                .sessionManagement((session) -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}
