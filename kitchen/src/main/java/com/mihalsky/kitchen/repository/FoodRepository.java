package com.mihalsky.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mihalsky.kitchen.domain.food.Food;

public interface FoodRepository extends JpaRepository<Food, Long>{
  
  Food findByName(String name);
  
}
