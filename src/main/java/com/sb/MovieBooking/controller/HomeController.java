package com.sb.MovieBooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/")
	public String home( ) {
		 log.info("HomeController.home() called");
	//	model.addAttribute("appName", "Movie Booking App");
	//	model.addAttribute("message", "Welcome to BookMyShow");
		 // ── Redirect root URL to index.html ──
		    // "forward:" serves the file directly without redirect
		    // This avoids the double request that was causing issues
	/**	 BEFORE (IF_REQUIRED):
			 Unauthenticated → Spring redirects to /login page

			 AFTER (STATELESS) without fix:
			 Unauthenticated → Http403ForbiddenEntryPoint → 403 error ❌
			 No login page redirect — just blocked!

			 AFTER (STATELESS) with fix:
			 Unauthenticated → our custom entry point → redirect to /index.html ✅*/
		 return "forward:/index.html";
		
	/**	@Controller + @GetMapping("/") is the standard Spring MVC pattern for view‑based controllers in Spring Boot 3/4.

		Model carries objects from controller to Thymeleaf; th:text reads them on the page.

		th:href="@{/login}" uses Thymeleaf’s URL expression to generate /login, which Security will later handle.
		*/
		
		


		
	}
}
