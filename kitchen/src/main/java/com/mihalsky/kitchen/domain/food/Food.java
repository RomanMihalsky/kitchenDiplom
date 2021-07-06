package com.mihalsky.kitchen.domain.food;

import java.io.Serializable;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mihalsky.kitchen.domain.Order;
import com.mihalsky.kitchen.domain.seat.SeatDateOrder;

import lombok.Data;

@Entity
@Data
public class Food implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, columnDefinition = "serial")
  private Long id;
  private String foodId;
  private Date addDate;
  private Date lastUpdateDate;
  private String foodImageUrl;
  private int calories;
  private float fats;
  private float carbs;
  private float proteins;
  private String name;
  private int weight;
  private int price;
  private int timeCook;
  @Column(columnDefinition = "text")
  private String description;
  private String category;
  private boolean isActive;
  private boolean isNotLocked;
  private boolean isFree;
  @JsonIgnore
  @ManyToMany(mappedBy = "wishFood",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
  private List<Order> orders;
}
