package com.mihalsky.kitchen.resource;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mihalsky.kitchen.domain.seat.Seat;
import com.mihalsky.kitchen.service.impl.SeatService;

@RestController
@RequestMapping(path = {"/seat"})
public class SeatResource {
  
  private SeatService seatService;
  
  public SeatResource(SeatService seatService) {
    this.seatService = seatService;
  }

  @GetMapping("/list")
  public ResponseEntity<List<Seat>> getAllSeats() {
      List<Seat> seats = this.seatService.findaAll();
      return new ResponseEntity<>(seats, OK);
  }
}







