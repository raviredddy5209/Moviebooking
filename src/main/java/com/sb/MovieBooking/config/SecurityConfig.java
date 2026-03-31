package com.sb.MovieBooking.config;
  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sb.MovieBooking.repository.UserRepository;
import com.sb.MovieBooking.service.CustomUserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

 

@Configuration
public class SecurityConfig {

	
	// private final UserRepository userRepository;
	// public SecurityConfig(UserRepository userRepository) {
	 //       this.userRepository = userRepository;
	 //   }
	 
	// @Bean
	  //  public UserDetailsService userDetailsService(UserRepository userRepository) {
	  //      return new CustomUserDetailsService(userRepository);
	  //  }
	private final JwtRequestFilter jwtRequestFilter;
 
	public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }
     	 	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
 //  @Bean
   // public UserDetailsService userDetailsService(UserRepository userRepository) {
     //   return new CustomUserDetailsService(userRepository);
     // ✅ REMOVED the @Bean userDetailsService() factory method — that was the cycle root.
        //    CustomUserDetailsService is already a bean via @Service; declaring it again here
        //    caused SecurityConfig to "own" it, making JwtRequestFilter depend on SecurityConfig.
   // }
     	 	
     	 	
   /*step3
    * DaoAuthenticationProvider does two things:

		Loads user from DB using UserDetailsService
				Compares passwords using BCryptPasswordEncoder
    * Step 4 — CustomUserDetailsService loads user from DB
    * after step Step 5 — BCrypt password comparison

		`DaoAuthenticationProvider` now compares passwords:
```
		User typed:       "1234"
			DB has:           "$2a$10$xyz..."  (BCrypt hash of "1234") BCrypt.matches("1234", "$2a$10$xyz...") → true ✅

    * 
    * BCrypt never decrypts — it re-hashes "1234" and compares the hashes. This is why plain text passwords never need to be stored.
    */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
       // provider.setUserDetailsService(userDetailsService);  // ✅ Setter exists
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
    	// ── Tell Spring what to do when unauthenticated user hits a protected page ──
        // Instead of showing 403 error page, redirect to index.html
        // This replaces the default Http403ForbiddenEntryPoint behaviour
        http.csrf(csrf -> csrf.disable())
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                	 // ── Only redirect GET requests to login page ──────────────
                    // POST/DELETE/PUT requests should return 401 JSON, not redirect
                    // Otherwise error responses loop back to index.html
                	if ("GET".equals(request.getMethod())) {
                        response.sendRedirect("/index.html");
                    } else {
                        // ── Return 401 JSON for API calls ─────────────────────
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Unauthorized\"}");
                    }
                    //response.sendRedirect("/index.html");
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html","/favicon.ico","/css/**", "/js/**", "/register", "/uploads/**", "/public/**",
                	    "/user/home", "/user/seats","/login","/postLogin").permitAll()
                .requestMatchers("/api/auth/**").permitAll()//jwt login endpoint public
             //// ── /public/** permitAll ───────────────────────────────────────────────
             // Any URL starting with /public is accessible without login
             // This covers:
             // /public/movies       → Now Showing page
             // /public/movies/{id}/shows → Show timings
             // /public/search?q=    → Search movies + theaters
             .requestMatchers("/public/**").permitAll()
                //   .requestMatchers("/admin/theaters/**").permitAll()
             // Protected APIs (JWT token required)
               // .requestMatchers("/admin/theatres").hasRole("ADMIN")
               // .requestMatchers("/admin/movies").hasRole("ADMIN")
                .requestMatchers("/uploads/**").permitAll() //mv imgs public
                .requestMatchers("/admin/dashboard", "/user/home").permitAll()
                
             // ✅ Admin protected APIs + pages
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ✅ User protected pages
                .requestMatchers("/user/**").hasRole("USER")
              //  .requestMatchers("/admin/theaters", "/admin/movies").hasRole("ADMIN")
              //  .requestMatchers("/admin/movies/**").hasRole("ADMIN")
              //  .requestMatchers("/admin/theaters/**").hasRole("ADMIN")
                 // images public
                //Role based pages
              //  .requestMatchers("/admin/**").hasRole("ADMIN")//ala dmin protected
             //   .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
        /*    .formLogin(form -> form
                .loginPage("/login")//matches getmapping  login
                .loginProcessingUrl("/doLogin")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/postLogin", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()*/
                /**
                 * logoutUrl("/logout"): Form POST to /logout logs out user.

					logoutSuccessUrl("/login?logout=true"): Redirect to login with success message.

						invalidateHttpSession(true): Destroys session.

						deleteCookies("JSESSIONID"): Deletes session cookie.
                 * SessionCreationPolicy.IF_REQUIRED: Creates session only when needed (traditional login/logout flow).
​
// Traditional login (session)
POST /doLogin → Cookie → /user/home.html

// API login (JWT)  
POST /api/auth/login → localStorage token → JS fetch('/admin/theatres')

                 * 
                 * */
                
                
                
         //   )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//jwt
            );
        
        		/**@Bean SecurityFilterChain is Spring Security 6 style (no deprecated WebSecurityConfigurerAdapter).
​			Bean SecurityFilterChain: New Security 6 style (no WebSecurityConfigurerAdapter).
​

					HttpSecurity http: Configures request matching, login, logout, sessions.

					csrf.disable(): Disables CSRF protection for simplicity (enable later with proper CSRF tokens).


				permitAll() for home, login, register, static files.

				hasRole("ADMIN") protects /admin/**; hasRole("USER") protects /user/**.
​ 		permitAll(): No login needed for home, login page, register, static files.

			hasRole("ADMIN"): Only admin can access /admin/** (e.g., /admin/theatres).

		hasRole("USER"): Only user can access /user/** (e.g., /user/bookings).

				anyRequest().authenticated(): Everything else needs login.


			loginPage("/login"): Custom login page at /login (handled by AuthController).

				loginProcessingUrl("/doLogin"): POST to /doLogin triggers authentication.

				usernameParameter("email"): Form field name="email" is the username.

				passwordParameter("password"): Form field name="password" is the password.

				defaultSuccessUrl("/postLogin", true): After login, always go to /postLogin.
	
				failureUrl("/login?error=true"): Failed login → /login?error=true (shown in Thymeleaf).

			    formLogin().loginProcessingUrl("/doLogin") handles POST /doLogin from login form.
​	

					defaultSuccessUrl("/postLogin", true) always redirects to /postLogin after login.*/
     // ── Add JWT filter before username/password filter ─────────────
        // JwtRequestFilter runs first on every request
        // It reads Authorization header, validates token, sets SecurityContext
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
