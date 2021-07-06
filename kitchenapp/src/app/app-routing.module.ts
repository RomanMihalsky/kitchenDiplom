import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthenticationGuard } from './guard/authentication.guard';
import { LoginComponent } from './login/login.component';
import { FoodComponent } from './main/food/food.component';
import { HomeComponent } from './main/home/home.component';
import { MainComponent } from './main/main.component';
import { PayComponent } from './main/pay/pay.component';
import { RegisterComponent } from './register/register.component';
import { UserComponent } from './user/user.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user/management', component: UserComponent, canActivate: [AuthenticationGuard] },
  { path: 'user/main', redirectTo: '/user/main/home', pathMatch: 'full' },
  { path: 'user/main', component: MainComponent, canActivate: [AuthenticationGuard],
    children:[
      {path:'home',component: HomeComponent,canActivate: [AuthenticationGuard]},
      {path:'pay',component: PayComponent,canActivate: [AuthenticationGuard]},
      {path:'food/:category',component: FoodComponent,canActivate: [AuthenticationGuard]}
    ]
   },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
