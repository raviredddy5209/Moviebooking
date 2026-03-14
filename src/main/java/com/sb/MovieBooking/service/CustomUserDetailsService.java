package com.sb.MovieBooking.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

 
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.UserRepository;

@Service
public class CustomUserDetailsService  implements UserDetailsService{
	
	
	
	@Autowired
	private   UserRepository userRepository;
	
	
 



	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
	
		
		/**    UserDetailsService loads user from DB for authentication.
​

SimpleGrantedAuthority(user.getRole().name()) gives ROLE_USER or ROLE_ADMIN.
​

User.builder() is Security 6 style (no deprecated constructor)
		*/
		
	User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
	
	
	Collection<GrantedAuthority> authorities = 
            List.of(new SimpleGrantedAuthority(user.getRole().name()));	
	
	
	
	return  org.springframework.security.core.userdetails.User.builder()
			.username(user.getEmail())
			.password(user.getPassword())
			.authorities(authorities)
			.build();
	}

}
