import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type';
import { Role } from 'src/app/enum/role';
import { BasketSum } from 'src/app/model/basket-sum';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { FileUploadStatus } from 'src/app/model/file-upload';
import { Food } from 'src/app/model/food';
import { FoodCount } from 'src/app/model/food-count';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { BasketService } from 'src/app/service/basket.service';
import { FoodService } from 'src/app/service/food.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-food',
  templateUrl: './food.component.html',
  styleUrls: ['./food.component.css']
})
export class FoodComponent implements OnInit {
  public category: string;
  public foods: Food[];
  public user: User;
  public refreshing: boolean;
  public selectedFood: Food;
  public fileName: string;
  public foodImage: File;
  private subscriptions: Subscription[] = [];
  public editFood = new Food();
  public fileStatus = new FileUploadStatus();
  public categoryTitle:string = '';

  constructor(private route: ActivatedRoute,private router: Router, private authenticationService: AuthenticationService,
    private userService: UserService,private basketService:BasketService ,private foodService:FoodService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.category =  this.route.snapshot.paramMap.get('category');
    this.defineCategory(this.category);
    this.user = this.authenticationService.getUserFromLocalCache();
    this.getFoods(true);
  }

  public getFoods(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.foodService.getFoods().subscribe(
        (response: Food[]) => {
          console.log(response);
          this.foodService.addFoodsToLocalCache(response);
          this.filterFood(response);
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );

  }

  public filterFood(foods:Food[]){
    this.foods = foods.filter(currentFood => currentFood.category == this.category);
  }

  public onSelectFood(selectedFood: Food): void {
    this.selectedFood = selectedFood;
    console.log(this.selectedFood);
    this.clickButton('openFoodInfo');
  }

  public onFoodImageChange(fileName: string, foodImage: File): void {
    this.fileName =  fileName;
    this.foodImage = foodImage;
  }

  public saveNewFood(): void {
    this.clickButton('new-food-save');
  }

  public onAddNewFood(foodForm: NgForm): void {
    const formData = this.foodService.createFoodFormDate('0',foodForm.value, this.foodImage);
    this.subscriptions.push(
      this.foodService.addFood(formData).subscribe(
        (response: Food) => {
          this.clickButton('new-food-close');
          this.getFoods(false);
          this.fileName = null;
          this.foodImage = null;
          foodForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `Еда (${response.name}) добавлена успешно.`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.foodImage = null;
        }
      )
      );
  }

  public onUpdateFood(): void {
    const formData = this.foodService.createFoodFormDate(this.editFood.id, this.editFood, this.foodImage);
    this.subscriptions.push(
      this.foodService.updateFood(formData).subscribe(
        (response: Food) => {
          this.clickButton('closeEditFoodModalButton');
          this.getFoods(false);
          this.fileName = null;
          this.foodImage = null;
          this.sendNotification(NotificationType.SUCCESS, `Еда (${response.name}) обновлена успешно.`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.foodImage = null;
        }
      )
      );
  }

  public onUpdateFoodImage(id:string): void {
    const formData = new FormData();
    formData.append('id', id);
    formData.append('foodImage', this.foodImage);
    this.subscriptions.push(
      this.foodService.updateFoodImage(formData).subscribe(
        (event: HttpEvent<any>) => {
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.fileStatus.status = 'done';
        }
      )
    );
  }

  public onDeleteFood(id: string): void {
    this.subscriptions.push(
      this.foodService.deleteFood(id).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getFoods(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onEditFood(editFood: Food): void {
    this.editFood = editFood;
    this.clickButton('openFoodEdit');
  }

  public searchFoods(searchTerm: string): void {
    const results: Food[] = [];
    for (const food of this.foodService.getFoodsFromLocalCache()) {
      if (food.name.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1){
          results.push(food);
      }
    }
    this.foods = results;
    if (results.length === 0 || !searchTerm) {
      this.foods = this.foodService.getFoodsFromLocalCache();
    }
  }

  toBasket(food:Food){
    let wishFood:Food[] = [];
    wishFood.push(food);
    let foodCount = new Map<number,FoodCount>();
    foodCount.set(+food.id,new FoodCount(1,food,food.price));
    let sumBasket = new BasketSum();
    sumBasket.sumPrice = food.price;
    sumBasket.sumCalories = food.calories;
    this.basketService.addToBasket(foodCount,wishFood,sumBasket);
    this.sendNotification(NotificationType.SUCCESS, `Еда добавлена в корзину.`);
  }

  public defineCategory(category:string){
    switch(category){
      case('Dinner'):{this.categoryTitle = 'Обеды'; break;};
      case('Dessert'):{this.categoryTitle = 'Десерты'; break;};
      case('Soup'):{this.categoryTitle = 'Супы'; break;};
      case('Salad'):{this.categoryTitle = 'Салаты'; break;};
      case('Drink'):{this.categoryTitle = 'Напитки'; break;};
      case('Fruit'):{this.categoryTitle = 'Фрукты'; break;};
    }
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() === Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'Произошла ошибка. Пожалуйста попробуйте снова.');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
