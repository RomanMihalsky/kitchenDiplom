package com.mihalsky.kitchen.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.mihalsky.kitchen.domain.food.Food;
import com.mihalsky.kitchen.exception.domain.FoodNameExistException;
import com.mihalsky.kitchen.exception.domain.FoodNotFoundException;
import com.mihalsky.kitchen.exception.domain.NotAnImageFileException;

public interface FoodService {
  
  List<Food> getFoods();
  
  Food findFoodById(Long id)throws FoodNotFoundException ;
  
  Food findByFoodName(String name)throws FoodNotFoundException ;
  
  Food addNewFood(
       MultipartFile foodImageUrl,
       int calories,
       float fats,
       float carbs,
       float proteins,
       String name,
       int weight,
       int price,
       String description,
       String category,
       boolean isActive,
       boolean isNotLocked,
       boolean isFree,
       int timeCook) throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException, MessagingException;

  Food updateFood(
      Long id,
      MultipartFile foodImageUrl,
      int calories,
      float fats,
      float carbs,
      float proteins,
      String name,
      int weight,
      int price,
      String description,
      String category,
      boolean isActive,
      boolean isNotLocked,
      boolean isFree,
      int timeCook) throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException;

  void deleteFood(Long id) throws IOException,FoodNotFoundException;

   Food updateFoodImage(Long id, MultipartFile profileImage) throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException;
}
