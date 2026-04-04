package com.sb.MovieBooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.model.LoginRequest;
import com.sb.MovieBooking.service.AuthService;
//Frontend → Controller → Service → Spring Security → JWT → Controller → Frontend
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private AuthService authService;
 /** CONTROLLER ROLE:
 // - Takes login request (email, password) from frontend
 // - Calls service
 // - Returns JWT token to frontend
    									*/
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
    	 // INPUT: LoginRequest (email, password)
        String token = authService.login(req);
     // OUTPUT: "Bearer <jwt-token>"
        return ResponseEntity.ok(token);
    }
    }
/*
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    
	@Autowired
    private AuthenticationManager authManager;
	 @Autowired
	    private JwtUtil jwtUtil;
	 //from axio
   //@RequestBody maps the JSON → LoginRequest object.
	// Then hands it to authManager.authenticate().
	 
	 
	// Step 3 — AuthenticationManager calls DaoAuthenticationProvider
	// authManager.authenticate() internally triggers your SecurityConfig bean:
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                req.getEmail(), req.getPassword()
            )
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();//Step 6 — JWT token generated and returned
        //JWT is built with email + role inside:
//        json{
//        	  "sub": "admin@example.com",
//        	  "roles": [{ "authority": "ROLE_ADMIN" }],
//        	  "iat": 1234567890,
//        	  "exp": 1234567890
//        	}
//        	```
        String jwtToken = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok("Bearer " + jwtToken);
    }
}
*/