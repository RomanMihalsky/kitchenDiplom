package com.mihalsky.kitchen.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mihalsky.kitchen.domain.seat.Seat;
import com.mihalsky.kitchen.repository.SeatRepository;

@Service
@Transactional
public class SeatService implements Serializable{
  private SeatRepository seatRepository;

  @Autowired
  public SeatService(SeatRepository seatRepository) {
    this.seatRepository = seatRepository;
  }
  
  public List<Seat> findaAll(){
    return this.seatRepository.findAll();
  }
  
  public Seat searchBySeatId(String seatId) {
    return this.seatRepository.findBySeatId(seatId);
  }
  
  public Seat findById(Long id) {
    return this.seatRepository.findById(id).get();
  }
}
