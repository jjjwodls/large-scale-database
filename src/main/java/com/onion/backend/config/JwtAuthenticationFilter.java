package com.onion.backend.config;

import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.user.service.BlackListService;
import com.onion.backend.user.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

    private final BlackListService blackListService;

    @Value("${jwt.token-name}")
    private String tokenName;


    public JwtAuthenticationFilter(
            JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
            BlackListService blackListService

    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.blackListService = blackListService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청의 Authorization 헤더에서 토큰 추출
        String bearerToken = resolveToken(request);

        // 토큰이 유효하고, 인증되지 않은 경우
        if (bearerToken != null && SecurityContextHolder.getContext().getAuthentication() == null && !blackListService.isTokenBlacklisted(bearerToken)) {

            String username = jwtUtil.extractUsername(bearerToken);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 토큰이 유효한지 검증
            if (jwtUtil.validateToken(bearerToken)) {
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

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        if(bearerToken == null){
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {
                    if(tokenName.equals(cookie.getName())){
                        return cookie.getValue();
                    }
                }
            }
        }

        return null;
    }


}
