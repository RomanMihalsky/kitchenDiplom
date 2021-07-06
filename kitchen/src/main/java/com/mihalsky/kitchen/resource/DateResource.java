package com.mihalsky.kitchen.resource;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mihalsky.kitchen.domain.seat.SeatDateOrder;
import com.mihalsky.kitchen.service.impl.DateService;

@RestController
@RequestMapping(path = {"/date"})
public class DateResource {
  private DateService dateService;

  public DateResource(DateService dateService) {
    this.dateService = dateService;
  }
  
  @GetMapping("/list")
  public ResponseEntity<List<SeatDateOrder>> getAllDates() {
      List<SeatDateOrder> dates = dateService.findAll();
      return new ResponseEntity<>(dates, OK);
  }
}
