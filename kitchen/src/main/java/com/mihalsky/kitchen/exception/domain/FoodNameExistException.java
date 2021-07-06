package com.mihalsky.kitchen.exception.domain;

public class FoodNameExistException extends Exception{
  public FoodNameExistException(String message) {
    super(message);
}
}
