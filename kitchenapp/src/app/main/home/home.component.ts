import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Category } from 'src/app/enum/category';
import { NotificationType } from 'src/app/enum/notification-type';
import { BasketSum } from 'src/app/model/basket-sum';
import { FileUploadStatus } from 'src/app/model/file-upload';
import { Food } from 'src/app/model/food';
import { FoodCount } from 'src/app/model/food-count';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { BasketService } from 'src/app/service/basket.service';
import { FoodService } from 'src/app/service/food.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  public foods: Food[];
  public user: User;
  public refreshing: boolean;
  public selectedFood: Food;
  private subscriptions: Subscription[] = [];
  public wishFood: Food[] = [];
  public foodCount = new Map<number,FoodCount>();
  public basketSum:BasketSum = new BasketSum();

  constructor(private router: Router, private authenticationService: AuthenticationService,
    private foodService: FoodService, private notificationService: NotificationService, private basketService: BasketService) {}

    ngOnInit(): void {
      this.user = this.authenticationService.getUserFromLocalCache();
      this.getFoods(true);
    }

    public getFoods(showNotification: boolean): void {
      this.refreshing = true;
      this.subscriptions.push(
        this.foodService.getFoods().subscribe(
          (response: Food[]) => {
            this.foodService.addFoodsToLocalCache(response);
            this.foods = response;
            this.refreshing = false;
            if (showNotification) {
              this.sendNotification(NotificationType.SUCCESS, `Еда успешно загружена.`);
            }
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            this.refreshing = false;
          }
        )
      );
  
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
    }

    public onPlusCount(food:Food){
      this.basketSum.sumPrice += food.price;
      this.basketSum.sumCalories += food.calories;
      this.foodCount.get(+food.id).count +=1;
      this.wishFood.push(food);
      this.foodCount.get(+food.id).sum += food.price;
    }

    public onCalk(food:Food){
      if(this.foodCount.get(+food.id) == undefined){
        this.foodCount.set(+food.id,new FoodCount(1,food,food.price));
      }else{
        this.foodCount.get(+food.id).sum += food.price;
        this.foodCount.get(+food.id).count += 1;
      }
      this.basketSum.sumPrice += food.price;
      this.basketSum.sumCalories += food.calories;
      this.wishFood.push(food);
    }

    public onRemoveFood(food:Food){
      this.basketSum.sumPrice -= food.price*this.foodCount.get(+food.id).count;
      this.basketSum.sumCalories -= food.calories*this.foodCount.get(+food.id).count;
      this.foodCount.delete(+food.id);
      this.wishFood = this.wishFood.filter(currenFood => 
      currenFood.id != food.id
      );
    }

    public toBasket(){
      this.basketService.addToBasket(this.foodCount,this.wishFood,this.basketSum);
      console.log("Home");
      console.log(this.foodCount);
      console.log(this.wishFood);
      console.log(this.basketSum);
      this.sendNotification(NotificationType.SUCCESS, `Еда добавлена в корзину.`);
    }

    public onSelectFood(selectedFood: Food): void {
      this.selectedFood = selectedFood;
      console.log(this.selectedFood);
      this.clickButton('openFoodInfo');
    }

    private clickButton(buttonId: string): void {
      document.getElementById(buttonId).click();
    }

    public toFoodCategory(foodCategory:string){
      this.router.navigate(['/user/main/food',foodCategory]);
    }

    private sendNotification(notificationType: NotificationType, message: string): void {
      if (message) {
        this.notificationService.notify(notificationType, message);
      } else {
        this.notificationService.notify(notificationType, 'Что-то пошло не так.');
      }
    }
}
