import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NotificationType } from 'src/app/enum/notification-type';
import { BasketSum } from 'src/app/model/basket-sum';
import { DateOrder } from 'src/app/model/date-order';
import { Food } from 'src/app/model/food';
import { FoodCount } from 'src/app/model/food-count';
import { Order } from 'src/app/model/order';
import { Seat } from 'src/app/model/seat';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { BasketService } from 'src/app/service/basket.service';
import { NotificationService } from 'src/app/service/notification.service';
import { OrderService } from 'src/app/service/order.service';
import { SeatService } from 'src/app/service/seat.service';

@Component({
  selector: 'app-pay',
  templateUrl: './pay.component.html',
  styleUrls: ['./pay.component.css']
})
export class PayComponent implements OnInit {
  public selectedPlace:string = 'away';
  public wishFood: Food[] = [];
  public foodCount = new Map<number,FoodCount>();
  public basketSum:BasketSum = new BasketSum();
  public place:string = '';
  public seats:Seat[];
  public choiceSeat:string = '';
  public dateVisit:Date;
  public dates:DateOrder[] = [];
  public user: User;
  public date:Date;
  public currentDate:Date;
  public hour:any;
  public minute:string;
  public second:string;
  public allSeats:Seat[] = [];
  public order:Order = new Order();
  public calculatedTime:number = 0;
  

  constructor(private basketService:BasketService,
    private notificationService: NotificationService,
    private seatService:SeatService,
    private orderService:OrderService,
    private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.loadStripe();
    this.user = this.authenticationService.getUserFromLocalCache();
    this.wishFood = this.basketService.getWishFood();
    this.foodCount = this.basketService.getFoodCount();
    this.basketSum = this.basketService.getBasketSum();
    this.getDates();
    this.getSeats();
    this.setDateInterval(1000);
    this.setSeatsInterval(60000);
  }

  public getDates(): void {
      this.seatService.getDates().subscribe(
        (response: DateOrder[]) => {
          this.seatService.addDatesToLocalCache(response);
          this.dates = response;
          console.log(response);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      );
  }
  
  public getSeats(): void {
      this.seatService.getSeats().subscribe(
        (response: Seat[]) => {
          this.seatService.addSeatsToLocalCache(response);
          this.allSeats = response;
          console.log(response);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      );
  }

  handler:any = null;
 
  makePay(amount: any) {    
    var handler = (<any>window).StripeCheckout.configure({
      key: 'pk_test_51Iw32TEwvaxBzN8lL28yOfEcND1ICGFO0Yt800LjbNl6dyGcqyj13KVhoSzI09zn1ZjXIhU3Y4YwK5DgYZx4dFD200fX13dsrS',
      locale: 'auto',
      token: function (token: any) {
        // You can access the token ID with `token.id`.
        // Get the token ID to your server-side code for use.
        console.log(token)
        document.getElementById('openMessage').click();
      }
    });

    handler.open({
      name: 'Ttk Kitchen',
      description: `${this.wishFood.length} товар(-ов) к оплате`,
      amount: 0,
      panelLabel:`Оплата ${amount} р.`
    });
 
  }
 
  loadStripe() {
     
    if(!window.document.getElementById('stripe-script')) {
      var s = window.document.createElement("script");
      s.id = "stripe-script";
      s.type = "text/javascript";
      s.src = "https://checkout.stripe.com/checkout.js";
      s.onload = () => {
        this.handler = (<any>window).StripeCheckout.configure({
          key: 'pk_test_51Iw32TEwvaxBzN8lL28yOfEcND1ICGFO0Yt800LjbNl6dyGcqyj13KVhoSzI09zn1ZjXIhU3Y4YwK5DgYZx4dFD200fX13dsrS',
          locale: 'auto',
          token: function (token: any) {
            // You can access the token ID with `token.id`.
            // Get the token ID to your server-side code for use.
            console.log(token)
            alert('Оплат прошла успешно!!');
          }
        });
      }
       
      window.document.body.appendChild(s);
    }
  }
 
  pay(){
    this.makePay(this.basketSum.sumPrice);
    let order:Order = new Order();
    order.id = 0;
    order.orderId = ''+ this.authenticationService.getUserFromLocalCache().userId + new Date().getTime();
    order.wishFood = this.wishFood;
    order.user = this.authenticationService.getUserFromLocalCache();
    order.price = this.basketSum.sumPrice;
    order.dateOrder = new Date(this.date);
    order.seat = null;
    if(this.selectedPlace == 'here'){
      order.seat = this.findSeat(this.choiceSeat);
    }
    this.orderService.addOrder(order).subscribe( (response: any) => {
      console.log(response);
    });
    this.calculatedTime = this.calculateTime(order.wishFood);
    this.order = order;
  }

  calculateTime(wishFood:Food[]):number{
    let max:number;
    max = wishFood[0].timeCook;
    for(let food of wishFood){
      if(food.timeCook > max){
        max = food.timeCook;
      }
    }
    return max;
  }


  findSeat(choiceSeat:string):Seat{
    for(let seat of this.allSeats){
      if(seat.seatId == choiceSeat){
        return seat;
      }
    }
    return null;
  }


  public radioChangeHandler(event:any){
    this.selectedPlace = event.target.value;
  }
  
  public radioChangeHandlerSeat(event:any){
    this.choiceSeat = event.target.value;
  }

  public checkPlace(place:string):string{
    for(let seat of this.allSeats){
      if(seat.seatId == place){
        if(seat.status == "Занято")
        return "seat-busy";
      }
    }
    return "seat"; 
  }

  setDateInterval(ml:number){
    setInterval(() =>{
      const date = new Date();
      this.updateDate(date);
    },ml)
  }

  setSeatsInterval(ml:number){
    setInterval(() =>{
      this.getSeats();
    },ml)
  }

  updateDate(date:Date){
    this.currentDate = date;
  }

  checkDate(date:Date):boolean{
    date = new Date(date);
    if(date.getHours() > this.currentDate.getHours()){
      return true;
    }
    if((date.getHours()+ 60 + date.getMinutes()) > (this.currentDate.getHours()+60+this.currentDate.getMinutes())){
      return true;
    }
    if((date.getHours()+ 60 + date.getMinutes() + 60 + date.getSeconds()) > (this.currentDate.getHours()+60+this.currentDate.getMinutes()+60 + this.currentDate.getSeconds())){
      return true;
    }


    return false;
  }

  public disabledSeat(date:DateOrder){
    for(let seat of date.seats){
      document.getElementById(`r${seat.seatId}`).setAttribute('disabled','true');
    }
    this.date = date.date;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'Что-то пошло не так.');
    }
  }
}
