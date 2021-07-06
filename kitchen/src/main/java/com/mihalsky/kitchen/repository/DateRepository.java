package com.mihalsky.kitchen.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mihalsky.kitchen.domain.seat.SeatDateOrder;

public interface DateRepository extends JpaRepository<SeatDateOrder, Long>{

  public SeatDateOrder findSeatDateOrderByDate(Date date);

}
