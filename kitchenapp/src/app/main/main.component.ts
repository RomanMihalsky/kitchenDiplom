import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OwlOptions, SlidesOutputData } from 'ngx-owl-carousel-o';
import { NotificationType } from '../enum/notification-type';
import { BasketSum } from '../model/basket-sum';
import { Food } from '../model/food';
import { FoodCount } from '../model/food-count';
import { BasketService } from '../service/basket.service';
import { NotificationService } from '../service/notification.service';
import { faPhoneAlt } from '@fortawesome/free-solid-svg-icons';
import { faVk } from '@fortawesome/free-brands-svg-icons';
import { faInstagram } from '@fortawesome/free-brands-svg-icons';
import { faFacebook} from '@fortawesome/free-brands-svg-icons';
import { faTwitter} from '@fortawesome/free-brands-svg-icons';
import { faOdnoklassniki} from '@fortawesome/free-brands-svg-icons';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  faPhoneAlt = faPhoneAlt;
  faVk = faVk;
  faInstagram = faInstagram;
  faFacebook = faFacebook;
  faTwitter = faTwitter;
  faOdnoklassniki = faOdnoklassniki;
  isActive = false;
  public wishFood: Food[] = [];
  public foodCount = new Map<number,FoodCount>();
  public basketSum:BasketSum = new BasketSum();

  constructor(private basketService:BasketService,private notificationService: NotificationService,private router:Router) { }

  ngOnInit(): void {
  }

  toBasket(){
    this.foodCount = this.basketService.getFoodCount();
    this.wishFood = this.basketService.getWishFood();
    this.basketSum = this.basketService.getBasketSum();
    console.log(this.foodCount);
    console.log(this.wishFood);
    console.log(this.basketSum);
    this.clickButton('openBasket');
  }

  toProfile(){
    this.router.navigateByUrl('/user/management');
  }

  public onMinusCount(food:Food){
    this.foodCount.get(+food.id).count -=1;
    this.foodCount.get(+food.id).sum -= food.price;
    this.basketSum.sumPrice -= food.price;
    this.basketSum.sumCalories -= food.calories;
    if(this.foodCount.get(+food.id).count == 0){
      this.foodCount.delete(+food.id);
    }
    let index = this.wishFood.findIndex(x => x === food);

    this.wishFood.splice(index,1);
    this.basketService.setBasketSum(this.basketSum);
    this.basketService.setFoodCount(this.foodCount);
    this.basketService.setWishFood(this.wishFood);
  }

  public onPlusCount(food:Food){
    this.basketSum.sumPrice += food.price;
    this.basketSum.sumCalories += food.calories;
    this.foodCount.get(+food.id).count +=1;
    this.wishFood.push(food);
    this.foodCount.get(+food.id).sum += food.price;
    this.basketService.setBasketSum(this.basketSum);
    this.basketService.setFoodCount(this.foodCount);
    this.basketService.setWishFood(this.wishFood);
  }

  public onRemoveFood(food:Food){
    this.basketSum.sumPrice -= food.price*this.foodCount.get(+food.id).count;
    this.basketSum.sumCalories -= food.calories*this.foodCount.get(+food.id).count;
    this.foodCount.delete(+food.id);
    this.wishFood = this.wishFood.filter(currenFood => 
      currenFood.id != food.id
      );
    console.log(this.wishFood);
    this.basketService.setBasketSum(this.basketSum);
    this.basketService.setFoodCount(this.foodCount);
    this.basketService.setWishFood(this.wishFood);
  }

  public toPay(){
    this.basketService.setBasketSum(this.basketSum);
    this.basketService.setWishFood(this.wishFood);
    this.basketService.setFoodCount(this.foodCount);
    this.router.navigateByUrl('/user/main/pay');
  }

  public toMain(){
    this.router.navigate(['/user/main']);
  }

  public toFoodCategory(foodCategory:string){
    this.router.navigate(['/user/main/food',foodCategory]);
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'Что-то пошло не так.');
    }
  }

}
