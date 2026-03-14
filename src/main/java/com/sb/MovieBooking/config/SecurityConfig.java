package com.sb.MovieBooking.config;
  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sb.MovieBooking.repository.UserRepository;
import com.sb.MovieBooking.service.CustomUserDetailsService;
 

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
	 
	 
	 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/register", "/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
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
                .permitAll()
                /**
                 * logoutUrl("/logout"): Form POST to /logout logs out user.

					logoutSuccessUrl("/login?logout=true"): Redirect to login with success message.

						invalidateHttpSession(true): Destroys session.

						deleteCookies("JSESSIONID"): Deletes session cookie.
                 * SessionCreationPolicy.IF_REQUIRED: Creates session only when needed (traditional login/logout flow).
​
                 * 
                 * */
                
                
                
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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

        return http.build();
    }
}
