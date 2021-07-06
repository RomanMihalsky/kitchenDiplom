package com.mihalsky.kitchen.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mihalsky.kitchen.domain.food.Food;
import com.mihalsky.kitchen.exception.domain.FoodNameExistException;
import com.mihalsky.kitchen.exception.domain.FoodNotFoundException;
import com.mihalsky.kitchen.exception.domain.NotAnImageFileException;
import com.mihalsky.kitchen.repository.FoodRepository;
import com.mihalsky.kitchen.service.EmailFoodService;
import com.mihalsky.kitchen.service.FoodService;

import lombok.extern.slf4j.Slf4j;

import static com.mihalsky.kitchen.constant.FileConstant.DEFAULT_FOOD_IMAGE_PATH;
import static com.mihalsky.kitchen.constant.FileConstant.DIRECTORY_CREATED;
import static com.mihalsky.kitchen.constant.FileConstant.DOT;
import static com.mihalsky.kitchen.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.mihalsky.kitchen.constant.FileConstant.FORWARD_SLASH;
import static com.mihalsky.kitchen.constant.FileConstant.JPG_EXTENSION;
import static com.mihalsky.kitchen.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.mihalsky.kitchen.constant.FileConstant.FOOD_FOLDER;
import static com.mihalsky.kitchen.constant.FileConstant.FOOD_IMAGE_PATH;
import static com.mihalsky.kitchen.constant.FoodImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;


@Service
@Slf4j
@Transactional
public class FoodServiceImpl implements FoodService{
  
  private FoodRepository foodRepository;
  private EmailFoodService emailFoodService;
  
  @Autowired
  public FoodServiceImpl(FoodRepository foodRepository,EmailFoodService emailFoodService) {
    this.foodRepository = foodRepository;
    this.emailFoodService = emailFoodService;
  }

  @Override
  public List<Food> getFoods() {
    return foodRepository.findAll();
  }

  @Override
  public Food findFoodById(Long id) throws FoodNotFoundException {
    return foodRepository.findById(id)
        .orElseThrow(() -> new FoodNotFoundException(FOUND_FOOD_BY_ID + id));
  }
  
  @Override
  public Food findByFoodName(String name) throws FoodNotFoundException {
    return foodRepository.findByName(name);
  }

  @Override
  public Food addNewFood(
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
      int timeCook) throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException, MessagingException {
    validateNewFoodName(EMPTY,name);
    Food food = new Food();
    food.setFoodId(generateFoodId());
    food.setName(name);
    food.setPrice(price);
    food.setWeight(weight);
    food.setAddDate(new Date());
    food.setLastUpdateDate(new Date());
    food.setActive(isActive);
    food.setNotLocked(isNotLocked);
    food.setFree(isFree);
    food.setDescription(description);
    food.setCalories(calories);
    food.setFats(fats);
    food.setCarbs(carbs);
    food.setProteins(proteins);
    food.setCategory(category);
    food.setTimeCook(timeCook);
    food.setFoodImageUrl(getTemporaryProfileImageUrl(food.getFoodId()));
    foodRepository.save(food);
    saveFoodImage(food, foodImageUrl);
    
    this.emailFoodService.sendMessageAboutNewFoodToUsers(food);
    return food;
  }

  @Override
  public Food updateFood(Long id, MultipartFile foodImageUrl, int calories, float fats, float carbs, float proteins,
      String name, int weight, int price, String description, String category, boolean isActive, boolean isNotLocked,
      boolean isFree,int timeCook) throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException {
    Food currentFood = findFoodById(id);
    currentFood =validateNewFoodName(currentFood.getName(),name);
    currentFood.setName(name);
    currentFood.setPrice(price);
    currentFood.setWeight(weight);
    currentFood.setLastUpdateDate(new Date());
    currentFood.setActive(isActive);
    currentFood.setNotLocked(isNotLocked);
    currentFood.setFree(isFree);
    currentFood.setDescription(description);
    currentFood.setCalories(calories);
    currentFood.setFats(fats);
    currentFood.setCarbs(carbs);
    currentFood.setProteins(proteins);
    currentFood.setTimeCook(timeCook);
    currentFood.setCategory(category);
    foodRepository.save(currentFood);
    saveFoodImage(currentFood, foodImageUrl);
    return currentFood;
  }

  @Override
  public void deleteFood(Long id) throws IOException,FoodNotFoundException {
    Food food = findFoodById(id);
    Path foodFolder = Paths.get(FOOD_FOLDER + food.getFoodId()).toAbsolutePath().normalize();
    FileUtils.deleteDirectory(new File(foodFolder.toString()));
    foodRepository.deleteById(id);
    
  }

  
  
  private void saveFoodImage(Food food, MultipartFile profileImage) throws IOException, NotAnImageFileException {
    if (profileImage != null) {
      if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
          throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
      }
      Path foodFolder = Paths.get(FOOD_FOLDER + food.getFoodId()).toAbsolutePath().normalize();
      if(!Files.exists(foodFolder)) {
          Files.createDirectories(foodFolder);
          log.info(DIRECTORY_CREATED + foodFolder);
      }
      Files.deleteIfExists(Paths.get(foodFolder + food.getFoodId() + DOT + JPG_EXTENSION));
      Files.copy(profileImage.getInputStream(), foodFolder.resolve(food.getFoodId() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
      food.setFoodImageUrl(setProfileImageUrl(food.getFoodId()));
      foodRepository.save(food);
      log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
  }
  }

  private String setProfileImageUrl(String foodName) {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(FOOD_IMAGE_PATH + foodName + FORWARD_SLASH
        + foodName + DOT + JPG_EXTENSION).toUriString();
  }
  
  private String getTemporaryProfileImageUrl(String FoodName) {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_FOOD_IMAGE_PATH + FoodName).toUriString();
  }
  
  private String generateFoodId() {
    return RandomStringUtils.randomNumeric(10);
  }
  
  private Food validateNewFoodName(String currentFoodName, String newFoodName) throws FoodNotFoundException, FoodNameExistException{
    Food foodByNewFoodName = findByFoodName(newFoodName);
    
    if(StringUtils.isNotBlank(currentFoodName)) {
        Food currentFood= findByFoodName(currentFoodName);
        if(currentFood == null) {
            throw new FoodNotFoundException(NO_FOOD_FOUND_BY_FOODNAME + currentFoodName);
        }
        if(foodByNewFoodName != null && !currentFood.getId().equals(foodByNewFoodName.getId())) {
            throw new FoodNameExistException(FOODNAME_ALREADY_EXISTS);
        }
       
        return currentFood;
    } else {
        if(foodByNewFoodName != null) {
            throw new FoodNameExistException(FOODNAME_ALREADY_EXISTS);
        }
        return null;
    }
  }

  @Override
  public Food updateFoodImage(Long id, MultipartFile profileImage)
      throws FoodNotFoundException, FoodNameExistException, IOException, NotAnImageFileException {
    Food current = findFoodById(id);
    
    Food food = validateNewFoodName(current.getName(), null);
    saveFoodImage(food, profileImage);
    return food;
  }
}
