package com.sb.MovieBooking.controller;
 

import com.sb.MovieBooking.model.LoginRequest;
import com.sb.MovieBooking.model.Role;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
 
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }

//    @PostMapping("/login")
//    public ResponseEntity<String>apiLogin(@RequestBody LoginRequest req, Authentication auth){
//    	
//    	if(auth.isAuthenticated()) {
//    		String token  = "Bearer fake-jwt-" + auth.getName();//Real jwt later
//    		return ResponseEntity.ok(token);
//    	}
//    	return ResponseEntity.status(401).body("unauthorised");
//    	//LoginRequest = DTO for JSON binding. No more error!
//    	
//    }
    
 
    /*
    		serves templates/auth/login.html custom login page
    		spring security calls  this for /login GET REQUESTS
    */

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }/* new user() becomes the backing bean for th:object="${user}" in the form
    
    */

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        return "redirect:/login?registered=true";
    }/*
    
    * @Modelattribute User user : binds form fields to User object
    * hashes password before saving
    * shows success message in login page
    *
    */

    @GetMapping("/postLogin")
    public String postLogin(Authentication auth, Model model) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            model.addAttribute("message", "Welcome Admin!");
            return "admin/dashboard";
        } else {
            model.addAttribute("message", "Welcome User!");
            return "user/home";
        }/*Authentication auth: Spring Security passes current user’s authorities.

				auth.getAuthorities() contains ROLE_ADMIN or ROLE_USER.

				Redirects to different dashboards based on role.*/
    }
}

								/*Browser → /login
    									↓
							AuthController.loginPage() → templates/auth/login.html
    									↓
						User fills form → POST /doLogin
    									↓
				SecurityConfig.formLogin() → CustomUserDetailsService.loadUserByUsername(email)
    									↓
								UserRepository.findByEmail(email) → DB query
    									↓
								Authentication success → /postLogin
    									↓
					AuthController.postLogin() → admin/dashboard.html or user/home.html

 * */
