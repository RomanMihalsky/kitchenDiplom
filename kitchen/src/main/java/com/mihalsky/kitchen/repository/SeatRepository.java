package com.mihalsky.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mihalsky.kitchen.domain.seat.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long>{
  
  public Seat findBySeatId(String seatId);
}
