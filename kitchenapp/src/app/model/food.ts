export class Food{
    public id:string;
    public foodId:string;
    public addDate:Date;
    public lastUpdateDate:Date;
    public foodImageUrl:string;
    public calories:number;
    public fats:number;
    public carbs:number;
    public proteins:number;
    public name:string;
    public weight:number;
    public price:number;
    public description:string;
    public category:string;
    public active:boolean
    public notLocked:boolean;
    public timeCook;
    public free:boolean;

    constructor(){
         this.id = '';
         this.foodId = '';
         this.addDate = null;
         this.lastUpdateDate = null;
         this.foodImageUrl = '';
         this.calories = null;
         this.fats= null;
         this.carbs= null;
         this.proteins= null;
         this.name = ' ';
         this.weight= null;
         this.price = null;
         this.timeCook = null;
         this. description = '';
         this.category = '';
         this.active = false;
         this.notLocked = false;
         this.free = false;
    }
}