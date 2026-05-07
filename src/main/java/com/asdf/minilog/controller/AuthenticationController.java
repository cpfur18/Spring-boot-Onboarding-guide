package com.asdf.minilog.controller;

import com.asdf.minilog.dto.AuthenticationRequestDto;
import com.asdf.minilog.dto.AuthenticationResponseDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.security.JwtUtil;
import com.asdf.minilog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody AuthenticationRequestDto authRequest) {
        // 서비스로 빼는게 좋을 것 같음
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(), authRequest.getPassword()));
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(authRequest.getUsername());
            UserResponseDto userResponseDto =
                    userService.getUserByUsername(userDetails.getUsername());
            return ResponseEntity.ok(
                    AuthenticationResponseDto.builder()
                            .jwt(jwtTokenUtil.generateToken(userDetails, userResponseDto.getId()))
                            .build());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication");
        }
    }
}
