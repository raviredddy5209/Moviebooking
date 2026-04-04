package com.sb.MovieBooking.service;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.model.Role;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {

        // INPUT: User (name, email, password from form)

        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password
        user.setRole(Role.ROLE_USER); // default role

        userRepository.save(user);

        // OUTPUT: nothing (user saved in DB)
    }
}