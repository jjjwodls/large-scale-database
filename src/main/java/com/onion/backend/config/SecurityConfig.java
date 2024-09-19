package com.onion.backend.config;

import com.onion.backend.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "api/users/signUp", "api/users/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 비활성화 (JWT 사용)

        ;
//                .formLogpin(Customizer.withDefaults());
//                .logout((logout) -> logout.permitAll()); // 기본 로그아웃 기능 사용
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager를 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and().build();

    }
}
