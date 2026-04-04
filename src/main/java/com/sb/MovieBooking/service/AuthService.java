package com.sb.MovieBooking.service;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.model.LoginRequest;
import com.sb.MovieBooking.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;
/** // SERVICE ROLE:
 // - Validates user credentials using Spring Security
 // - Generates JWT token if valid
 // - Returns token to controller*/
    public String login(LoginRequest req) {
    	  // INPUT: email + password
        Authentication auth = authManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                req.getEmail(), req.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String jwtToken = jwtUtil.generateToken(userDetails);
     // OUTPUT: "Bearer <jwt-token>"
        return "Bearer " + jwtToken;
    }
}