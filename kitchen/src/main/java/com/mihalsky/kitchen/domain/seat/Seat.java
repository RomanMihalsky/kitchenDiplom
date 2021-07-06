package com.mihalsky.kitchen.domain.seat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(value= {"dates"})
public class Seat implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, columnDefinition = "serial")
  private Long id;
  
  private String seatId;
  
  private String status;
  
  @ManyToMany(mappedBy = "seats",fetch = FetchType.LAZY)
  private List<SeatDateOrder> dates;
  
  public void addDate(SeatDateOrder date) {
    if(this.dates == null) {
      this.dates = new ArrayList<>();
    }
    this.dates.add(date);
  }
}
