package com.mihalsky.kitchen.domain.seat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
public class SeatDateOrder implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, columnDefinition = "serial")
  private Long id;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow" )
  private Date date;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable( name = "date_seats",
     joinColumns = {@JoinColumn(name = "date_id",referencedColumnName = "id")},
     inverseJoinColumns = {@JoinColumn(name = "seat_id",referencedColumnName = "id")})
  private List<Seat> seats;

  public void addSeat(Seat seat) {
    if(this.seats == null) {
      this.seats = new ArrayList<>();
    }
    this.seats.add(seat);
  }
}
