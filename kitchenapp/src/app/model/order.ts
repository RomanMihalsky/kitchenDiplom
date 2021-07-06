import { Food } from "./food";
import { Seat } from "./seat";
import { User } from "./user";

export class Order{
  public id:number;
  public orderId?: string;
  public wishFood:Food[];
  public price:number;
  public dateOrder:Date;
  public user: User;
  public seat:Seat;
}