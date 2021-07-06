import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Order } from '../model/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.host}/order/list`);
  }

  public addOrder(order:Order): Observable<Order> {
    console.log(order);
    return this.http.post<Order>(`${this.host}/order/add`, order);
  }

  public createOrderFormDate(id:string,order: Order) {
    console.log(order);
    const formData = new FormData();
    
  }
}
