package com.mihalsky.kitchen.resource;

import static com.mihalsky.kitchen.constant.FileConstant.FORWARD_SLASH;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mihalsky.kitchen.domain.HttpResponse;
import com.mihalsky.kitchen.domain.food.Food;
import com.mihalsky.kitchen.exception.domain.FoodNameExistException;
import com.mihalsky.kitchen.exception.domain.FoodNotFoundException;
import com.mihalsky.kitchen.exception.domain.NotAnImageFileException;
import com.mihalsky.kitchen.service.FoodService;
import static com.mihalsky.kitchen.constant.FileConstant.*;


@RestController
@RequestMapping(path = {"/food"})
public class FoodResource {
  
  private FoodService foodService;
  public static final String FOOD_DELETED_SUCCESSFULLY = "Food deleted successfully";
  
  @Autowired
  public FoodResource(FoodService foodService) {
    this.foodService = foodService;
  }
  
  @PostMapping("/add")
  public ResponseEntity<Food> addNewFood( @RequestParam("calories") String calories, @RequestParam("fats")String fats, @RequestParam("carbs")String carbs, @RequestParam("proteins")String proteins,  @RequestParam("name")String name,@RequestParam("weight")String weight, @RequestParam("price")String price, @RequestParam("description")String description, 
      @RequestParam("category")String category, 
      @RequestParam("isActive")String isActive, 
      @RequestParam("isNotLocked")String isNotLocked,
      @RequestParam("isFree")String isFree,
      @RequestParam(value = "foodImageUrl", required = false)MultipartFile foodImageUrl,
      @RequestParam("timeCook")String timeCook) throws IOException, NotAnImageFileException, MessagingException, NumberFormatException, FoodNotFoundException, FoodNameExistException {
    Food newFood = foodService.addNewFood(
          foodImageUrl,
          Integer.parseInt(calories),
          Float.parseFloat(fats),
          Float.parseFloat(carbs),
          Float.parseFloat(proteins),
          name,
          Integer.parseInt(weight),
          Integer.parseInt(price),
          description,
          category,
          Boolean.parseBoolean(isNotLocked),
          Boolean.parseBoolean(isActive),
          Boolean.parseBoolean(isFree),
          Integer.parseInt(timeCook));
      return new ResponseEntity<>(newFood, OK);
  }
  
  @PostMapping("/update")
  public ResponseEntity<Food> updateFood( 
      @RequestParam("id") String id,
      @RequestParam("calories") String calories, 
      @RequestParam("fats")String fats, 
      @RequestParam("carbs")String carbs, 
      @RequestParam("proteins")String proteins, 
      @RequestParam("name")String name,
      @RequestParam("weight")String weight, 
      @RequestParam("price")String price, 
      @RequestParam("description")String description, 
      @RequestParam("category")String category, 
      @RequestParam("isActive")String isActive, 
      @RequestParam("isNotLocked")String isNotLocked,
      @RequestParam("isFree")String isFree,
      @RequestParam(value = "foodImageUrl",required = false)MultipartFile foodImageUrl,
      @RequestParam("timeCook")String timeCook) throws IOException, NotAnImageFileException, MessagingException, NumberFormatException, FoodNotFoundException, FoodNameExistException {
      System.out.println(id);
      Food newFood = foodService.updateFood(
          Long.parseLong(id),
          foodImageUrl,
          Integer.parseInt(calories),
          Float.parseFloat(fats),
          Float.parseFloat(carbs),
          Float.parseFloat(proteins),
          name,
          Integer.parseInt(weight),
          Integer.parseInt(price),
          description,
          category,
          Boolean.parseBoolean(isNotLocked),
          Boolean.parseBoolean(isActive),
          Boolean.parseBoolean(isFree),
          Integer.parseInt(timeCook));
      return new ResponseEntity<>(newFood, OK);
  }
  
  @GetMapping("/list")
  public ResponseEntity<List<Food>> getAllFoods() {
      List<Food> foods = foodService.getFoods();
      return new ResponseEntity<>(foods, OK);
  }
  
  @GetMapping("/find/{id}")
  public ResponseEntity<Food> getFood(@PathVariable("id") String id) throws NumberFormatException, FoodNotFoundException {
      Food food = foodService.findFoodById(Long.parseLong(id));
      return new ResponseEntity<>(food, OK);
  }
  
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasAnyAuthority('user:delete')")
  public ResponseEntity<HttpResponse> deleteFood(@PathVariable("id") String id) throws IOException, NumberFormatException, FoodNotFoundException {
      foodService.deleteFood(Long.parseLong(id));
      return response(OK, FOOD_DELETED_SUCCESSFULLY);
  }
  
  @PostMapping("/updateFoodImage")
  public ResponseEntity<Food> updateFoodImage(@RequestParam("id") String id, @RequestParam(value = "foodImage") MultipartFile profileImage) throws IOException, NotAnImageFileException, NumberFormatException, FoodNotFoundException, FoodNameExistException {
      Food food = foodService.updateFoodImage(Long.parseLong(id), profileImage);
      return new ResponseEntity<>(food, OK);
  }
  
  private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
            message), httpStatus);
  }
  
  @GetMapping(path = "/image/{fileNameNum}/{fileName}", produces = IMAGE_JPEG_VALUE)
  public byte[] getProfileImage(@PathVariable("fileNameNum") String fileNameNum, @PathVariable("fileName") String fileName) throws IOException {
      return Files.readAllBytes(Paths.get(FOOD_FOLDER + fileNameNum + FORWARD_SLASH + fileName));
  }
  
}
