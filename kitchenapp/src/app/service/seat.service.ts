import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { DateOrder } from '../model/date-order';
import { Seat } from '../model/seat';

@Injectable({
  providedIn: 'root'
})
export class SeatService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getDates(): Observable<DateOrder[]> {
    return this.http.get<DateOrder[]>(`${this.host}/date/list`);
  }
  
  public getSeats(): Observable<Seat[]> {
    return this.http.get<Seat[]>(`${this.host}/seat/list`);
  }

  public addDatesToLocalCache(dates: DateOrder[]): void {
    localStorage.setItem('dates', JSON.stringify(dates));
  }
  
  public addSeatsToLocalCache(seats: Seat[]): void {
    localStorage.setItem('seats', JSON.stringify(seats));
  }
}
