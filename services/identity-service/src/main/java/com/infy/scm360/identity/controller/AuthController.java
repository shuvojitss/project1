package com.infy.scm360.identity.controller;

import com.infy.scm360.identity.dto.LoginRequest;
import com.infy.scm360.identity.dto.LoginResponse;
import com.infy.scm360.identity.dto.RefreshTokenRequest;
import com.infy.scm360.identity.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.refresh(request, ip));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails principal) {
        if (principal != null) {
            authService.logout(principal.getUsername());
        }
        return ResponseEntity.noContent().build();
    }
}
