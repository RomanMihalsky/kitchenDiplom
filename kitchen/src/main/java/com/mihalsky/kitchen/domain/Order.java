package com.mihalsky.kitchen.domain;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mihalsky.kitchen.domain.food.Food;
import com.mihalsky.kitchen.domain.seat.Seat;

import lombok.Data;

@Entity
@Data
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, columnDefinition = "serial")
  private Long id;
  private String orderId;
  @OneToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
  @JoinColumn(name = "seat_id", nullable = true,unique = false)
  private Seat seat;
  private Date dateOrder;
  private int price;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable( name = "orders_food",
     joinColumns = {@JoinColumn(name = "order_id",referencedColumnName = "id")},
     inverseJoinColumns = {@JoinColumn(name = "food_id",referencedColumnName = "id")})
  private List<Food> wishFood;
  @OneToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
  @JoinColumn(name = "user_id",unique = false)
  private User user;
}















