package com.sb.MovieBooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.service.TheaterService;

@RestController
@RequestMapping("/admin/theaters")
public class TheaterController {
    
    @Autowired
    private TheaterService theaterService; // ── Service layer for business logic ──

    // ── CREATE Theater ───────────────────────────────────────────────
    // URL: POST /admin/theaters
    // Saves new theater into DB
    @PostMapping
    public Theater create(@RequestBody Theater theater) {

        // ── Delegating to service ──
        return theaterService.createTheater(theater);
    }

    /**
     * Because the JavaScript call runs inside your logged-in browser session, but Postman does not share that session or cookies.

        What happens with the JavaScript call
        You log in as admin in Chrome.

        Chrome stores the JSESSIONID cookie for localhost:8081.

        When you run fetch('/admin/theaters', ...) in DevTools Console, the browser automatically sends that cookie with the request.

        Spring Security sees ROLE_ADMIN, allows the request, controller runs, repo.save(theater) executes, Hibernate does INSERT, and MySQL saves the theater.

        What happens with Postman
        Postman is a separate client, with no knowledge of your browser cookie.

        When you send POST http://localhost:8081/admin/theaters from Postman:

        It usually does not send a valid JSESSIONID cookie (unless you configure it).

        Spring Security treats you as anonymous, sees /admin/** requires ROLE_ADMIN, and either redirects to /login or returns 302/403.

        The controller method is never executed, so no INSERT in the DB.

        So:

        Browser console POST  = logged-in admin + JSESSIONID  → SAVE works
        Postman POST          = anonymous (no session/role)   → SAVE blocked 

        ....................Browser Console (logged in): fetch('/admin/theaters') → JSON
        Postman (no login): POST → login.html page
     */

    // ── GET all theaters ─────────────────────────────────────────────
    // URL: GET /admin/theaters
    // Returns list of all theaters
    @GetMapping
    public List<Theater> list() {

        // ── Delegating to service ──
        return theaterService.getAllTheaters();
    }

    // ── DELETE Theater ───────────────────────────────────────────────
    // URL: DELETE /admin/theaters/{id}
    // Deletes theater by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        // ── Delegating to service ──
        return theaterService.deleteTheater(id);
    }
}