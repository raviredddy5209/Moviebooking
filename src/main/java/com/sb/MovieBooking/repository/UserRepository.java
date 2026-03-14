package com.sb.MovieBooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sb.MovieBooking.model.User;
@Repository
public interface UserRepository extends JpaRepository<User	, Long>{
	Optional <User>  findByEmail(String email);
	//findByEmail auto‑generates SQL SELECT * FROM users WHERE email =?

}
