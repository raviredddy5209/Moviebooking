package com.sb.MovieBooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.repository.TheaterRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/admin/theaters")
@Transactional  
public class TheaterController {
    
    @Autowired
    private TheaterRepository repo;
    
    
    
    
    @PostMapping
    public Theater create(@RequestBody Theater theater) {
        return repo.save(theater);
    }
    			/**
    			 * Because the JavaScript call runs inside your logged‑in browser session, but Postman does not share that session or cookies.

						What happens with the JavaScript call
						You log in as admin in Chrome.

					Chrome stores the JSESSIONID cookie for localhost:8081.
​

						When you run fetch('/admin/theaters', ...) in DevTools Console, the browser automatically sends that cookie with the request.

					Spring Security sees ROLE_ADMIN, allows the request, controller runs, repo.save(theater) executes, Hibernate does INSERT, and MySQL saves the theater.
​

					What happens with Postman
						Postman is a separate client, with no knowledge of your browser cookie.
​

						When you send POST http://localhost:8081/admin/theaters from Postman:

						It usually does not send a valid JSESSIONID cookie (unless you configure it).
​

						Spring Security treats you as anonymous, sees /admin/** requires ROLE_ADMIN, and either redirects to /login or returns 302/403.

				The controller method is never executed, so no INSERT in the DB.

					So:

 
		Browser console POST  = logged-in admin + JSESSIONID  → SAVE works
		Postman POST          = anonymous (no session/role)   → SAVE blocked 
    			 * 
    			 * 
    			 *....................Browser Console (logged in): fetch('/admin/theaters') → JSON
    Postman (no login): POST → login.html page

    			 * 
    			 * */
    			 
    
    
    @GetMapping
    public List<Theater> list() {
        return repo.findAll();
    }
}

