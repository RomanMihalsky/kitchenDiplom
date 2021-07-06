import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Food } from '../model/food';

@Injectable({
  providedIn: 'root'
})
export class FoodService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getFoods(): Observable<Food[]> {
    return this.http.get<Food[]>(`${this.host}/food/list`);
  }

  public addFood(formData: FormData): Observable<Food> {
    return this.http.post<Food>(`${this.host}/food/add`, formData);
  }

  public updateFood(formData: FormData): Observable<Food> {
    return this.http.post<Food>(`${this.host}/food/update`, formData);
  }

  public updateFoodImage(formData: FormData): Observable<HttpEvent<Food>> {
    return this.http.post<Food>(`${this.host}/food/updateFoodImage`, formData,
    {reportProgress: true,
      observe: 'events'
    });
  }

  public deleteFood(id: string): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(`${this.host}/food/delete/${id}`);
  }

  public addFoodsToLocalCache(foods: Food[]): void {
    localStorage.setItem('foods', JSON.stringify(foods));
  }

  public getFoodsFromLocalCache(): Food[] {
    if (localStorage.getItem('foods')) {
        return JSON.parse(localStorage.getItem('foods'));
    }
    return null;
  }

  public createFoodFormDate(id:string,food: Food, foodImage: File): FormData {
    console.log(food);
    const formData = new FormData();
    if(+id > 0){
    formData.append('id', food.id);
    }
    formData.append('calories',JSON.stringify(+food.calories));
    formData.append('carbs',JSON.stringify(+food.carbs));
    formData.append('fats',JSON.stringify(+food.fats));
    formData.append('proteins',JSON.stringify(+food.proteins));
    formData.append('weight',JSON.stringify(+food.weight));
    formData.append('price',JSON.stringify(+food.price));
    formData.append('timeCook',JSON.stringify(+food.timeCook));
    formData.append('isActive',JSON.stringify(food.active));
    formData.append('isNotLocked',JSON.stringify(!!food.notLocked));
    formData.append('isFree',JSON.stringify(!!food.free));
    formData.append('name',food.name);
    formData.append('description',food.description);
    formData.append('category',food.category);
    formData.append('foodImageUrl',foodImage);
    return formData;
  }
}
