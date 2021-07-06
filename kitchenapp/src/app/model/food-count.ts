import { Food } from "./food";

export class FoodCount{
    public food:Food;
    public count:number;
    public sum:number;

    constructor(count:number,food:Food,sum:number){
        this.count = count;
        this.food = food;
        this.sum = sum
    }
}