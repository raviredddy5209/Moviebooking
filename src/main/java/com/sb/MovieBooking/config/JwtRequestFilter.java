package com.sb.MovieBooking.config;
import com.sb.MovieBooking.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
    	System.out.println(" JWT FILTER TRIGGERED - Header: " + request.getHeader("Authorization"));
        // 👇 THIS IS WHERE BEARER PARSING HAPPENS
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 👇 EXACT CODE YOU ASKED ABOUT
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);           // "Bearer eyJ..." → "eyJ..."
            try {
                username = jwtUtil.extractUsername(jwt);      // Parse JWT → get username
            } catch (Exception e) {
                // Invalid JWT → username stays null
            }
        }

        // If we have valid username + no existing auth
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate JWT signature + expiration
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // ✅ Mark user as authenticated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);  // Continue to controller
    }
}
/*
 * flow When Postman Calls /admin/theatres
 * 1. Postman: Authorization: Bearer eyJhbGciOiJIUzI1Ni...
2. Filter: authorizationHeader = "Bearer eyJhbGciOiJIUzI1Ni..."
3. 👇 if (startsWith("Bearer ")) → true
4. jwt = substring(7) → "eyJhbGciOiJIUzI1Ni..."
5. username = jwtUtil.extractUsername(jwt) → "admin@example.com"
6. userDetailsService.loadUserByUsername("admin...") → loads from DB
7. jwtUtil.validateToken(jwt, userDetails) → checks signature + expiration
8. SecurityContext: user is now authenticated
9. Controller runs → returns JSON

 */
