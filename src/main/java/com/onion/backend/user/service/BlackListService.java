package com.onion.backend.user.service;

import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.user.domain.JwtBlackList;
import com.onion.backend.user.infrastructure.JwtBlackListRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class BlackListService {

    private final JwtBlackListRepository jwtBlackListRepository;

    private final JwtUtil jwtUtil;

    public BlackListService(JwtBlackListRepository jwtBlackListRepository, JwtUtil jwtUtil) {
        this.jwtBlackListRepository = jwtBlackListRepository;
        this.jwtUtil = jwtUtil;
    }

    public void addTokenToBlacklist(String token, LocalDateTime expiresAt, String username) {
        JwtBlackList blacklistToken = new JwtBlackList();
        blacklistToken.setToken(token);
        blacklistToken.setCreatedAt(LocalDateTime.now());
        blacklistToken.setExpiresAt(expiresAt);
        blacklistToken.setUsername(username);

        jwtBlackListRepository.save(blacklistToken);
    }

    public boolean isTokenBlacklisted(String token) {
        String username = jwtUtil.extractUsername(token);
        Optional<JwtBlackList> blackListToken = jwtBlackListRepository.findFirstByUsernameOrderByExpiresAtDesc(username);
        if(blackListToken.isEmpty()){
            return false;
        }

        LocalDateTime currentTokenExpireTime = jwtUtil.extractExpiration(token).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return blackListToken.get().getExpiresAt().isAfter(currentTokenExpireTime);

    }

}
