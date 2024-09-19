package com.onion.backend.user.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class CustomUserDetail implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String username;
    private String password;

    public CustomUserDetail(
            Long id,
            String email,
            String username,
            String password
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;

    }

    public static UserDetails toCustomUserDetails(User user) {
        return new CustomUserDetail(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
