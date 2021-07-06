import { Injectable } from '@angular/core';
import { BasketSum } from '../model/basket-sum';
import { Food } from '../model/food';
import { FoodCount } from '../model/food-count';

@Injectable({
  providedIn: 'root'
})
export class BasketService {
  private wishFood: Food[] = [];
  private foodCount = new Map<number,FoodCount>();
  private basketSum:BasketSum = new BasketSum();
  
  constructor() { }

  addToBasket(foodCount: Map<number, FoodCount>, wishFood: Food[], basketSum: BasketSum) {
    this.basketSum.sumPrice += basketSum.sumPrice;
    this.basketSum.sumCalories += basketSum.sumCalories;
    foodCount.forEach((value:FoodCount,key:number) =>{
      if(this.foodCount.get(+value.food.id)!= undefined){
        this.foodCount.get(+value.food.id).count +=value.count;
        this.foodCount.get(+value.food.id).sum +=value.sum;
      }else{
        this.foodCount.set(key,new FoodCount(value.count,value.food,value.sum));
      }
    })
    this.wishFood = this.wishFood.concat(wishFood);
  }

  public getFoodCount(): Map<number,FoodCount> {
    return this.foodCount;
  }

  public getWishFood(): Food[]{
    return this.wishFood;
  }
  
  public getBasketSum():BasketSum{
    return this.basketSum;
  }

  public setFoodCount(foodCount:Map<number,FoodCount>){
    this.foodCount = foodCount;
  }

  public setWishFood(wishFood: Food[] ){
   this.wishFood = wishFood;
  }
  
  public setBasketSum(basketSum:BasketSum){
    this.basketSum = basketSum;
  }  
}
