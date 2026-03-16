package com.sb.MovieBooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sb.MovieBooking.entity.Theater;
@Repository
public interface TheaterRepository  extends JpaRepository<Theater, Long>{

}
