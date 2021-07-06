package com.mihalsky.kitchen.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mihalsky.kitchen.domain.seat.SeatDateOrder;
import com.mihalsky.kitchen.repository.DateRepository;

@Service
@Transactional
public class DateService {
  private DateRepository dateRepository;

  @Autowired
  public DateService(DateRepository dateRepository) {
    this.dateRepository = dateRepository;
  }
  
  public List<SeatDateOrder> findAll() {
    return this.dateRepository.findAll();
  }
  
  public SeatDateOrder serchByDate(Date date) {
    return this.dateRepository.findSeatDateOrderByDate(date);
  }

  public void updateDate(SeatDateOrder dateOrder) {
   this.dateRepository.save(dateOrder);
  }
}
