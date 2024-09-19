package com.onion.backend.config;

import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.user.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;


    public JwtAuthenticationFilter(
            JwtUtil jwtUtil, CustomUserDetailsService userDetailsService
    ){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청의 Authorization 헤더에서 토큰 추출
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Authorization 헤더가 "Bearer "로 시작할 때만 토큰을 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                // 만료된 토큰 처리
                logger.warn("JWT Token has expired");
            }
        }

        // 토큰이 유효하고, 인증되지 않은 경우
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 토큰이 유효한지 검증
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증 정보를 설정
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }


}
