import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './auth/auth.interceptor';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NotificationModule } from './notification.module';
import { NotificationService } from './service/notification.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { UserComponent } from './user/user.component';
import { RegisterComponent } from './register/register.component';
import { MainComponent } from './main/main.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CarouselModule } from 'ngx-owl-carousel-o';
import {MatGridListModule} from '@angular/material/grid-list';
import { HomeComponent } from './main/home/home.component';
import {MatIconModule} from '@angular/material/icon';
import { PayComponent } from './main/pay/pay.component';
import {MatSelectModule} from '@angular/material/select';
import { FoodComponent } from './main/food/food.component';
import { DigitalClockComponent } from './main/digital-clock/digital-clock.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {MatMenuModule} from '@angular/material/menu';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserComponent,
    RegisterComponent,
    MainComponent,
    HomeComponent,
    PayComponent,
    FoodComponent,
    DigitalClockComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    CarouselModule,
    MatGridListModule,
    MatIconModule,
    MatSelectModule,
    FontAwesomeModule,
    MatMenuModule
  ],
  providers: [NotificationService,AuthenticationGuard,AuthenticationService, UserService,
     {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
