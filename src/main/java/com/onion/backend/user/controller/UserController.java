package com.onion.backend.user.controller;

import com.onion.backend.user.controller.dto.SignInUser;
import com.onion.backend.user.controller.dto.SignUpUser;
import com.onion.backend.user.domain.User;
import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.user.service.CustomUserDetailsService;
import com.onion.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    private AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    private CustomUserDetailsService userDetailsService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> createUser(@RequestBody SignUpUser signUpUser) {
        User user = userService.createUser(signUpUser.getUsername(), signUpUser.getEmail(), signUpUser.getPassword());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); // Use this for hard delete
        return ResponseEntity.noContent().build();
    }

    // 로그인 요청 처리 (토큰 발급)
    @PostMapping("/login")
    public String login(@RequestBody SignInUser signInUser) throws AuthenticationException {
        // 사용자 인증
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInUser.getUsername(), signInUser.getPassword())
            );

            // 인증된 사용자가 있으면 JWT 토큰 생성
            if (authentication.isAuthenticated()) {
                return jwtUtil.generateToken(signInUser.getUsername());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return null;

    }

    @GetMapping("/token/validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateToken(
            @RequestParam String token
    ){
        if(!jwtUtil.validateToken(token)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }

}
