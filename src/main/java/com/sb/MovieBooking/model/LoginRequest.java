package com.sb.MovieBooking.model;

//package com.sb.MovieBooking.model;  // or dto package

public class LoginRequest {
    private String email;
    private String password;
    
    public LoginRequest() {
		 
	}
    
	public LoginRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	// Getters/Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
