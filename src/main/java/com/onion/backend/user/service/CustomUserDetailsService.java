package com.onion.backend.user.service;


import com.onion.backend.user.domain.CustomUserDetail;
import com.onion.backend.user.domain.User;
import com.onion.backend.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;



    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(("User not found username : " + username)));
        return CustomUserDetail.toCustomUserDetails(user);

    }
}
