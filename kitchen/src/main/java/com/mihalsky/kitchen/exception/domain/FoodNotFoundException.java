package com.mihalsky.kitchen.exception.domain;

public class FoodNotFoundException extends Exception{
  public FoodNotFoundException(String message) {
    super(message);
}
}
