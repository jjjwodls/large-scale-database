package com.onion.backend.user.infrastructure;

import com.onion.backend.user.domain.JwtBlackList;
import com.onion.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtBlackListRepository extends JpaRepository<JwtBlackList,Long> {

    Optional<JwtBlackList> findByToken(String token);

    Optional<JwtBlackList> findFirstByUsernameOrderByExpiresAtDesc(String username);
}
