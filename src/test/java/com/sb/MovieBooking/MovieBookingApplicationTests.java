package com.sb.MovieBooking;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class MovieBookingApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void contextLoads() {
    }  // Keep this - tests app starts
    
    @Test
    void testRegisterEndpoint() {
        // Test /register works
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:8081/register", 
            "email=test@movie.com&password=test123", 
            String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

