package com.sb.MovieBooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.model.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    
	@Autowired
    private AuthenticationManager authManager;
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                req.getEmail(), req.getPassword()
            )
        );
        String token = "Bearer " + auth.getName() + "-token";
        return ResponseEntity.ok(token);
    }
}
