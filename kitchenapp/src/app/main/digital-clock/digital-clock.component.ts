import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-digital-clock',
  templateUrl: './digital-clock.component.html',
  styleUrls: ['./digital-clock.component.css']
})
export class DigitalClockComponent implements OnInit {

  private days:string[] = ['Воскресенье','Понедельник','Вторник','Среда','Четверг','Пятница','Суббота']; 
  private date = new Date();
  public hour:any;
  public minute:string;
  public second:string;
  public day:string;

  constructor() { }

  ngOnInit(): void {
    setInterval(() =>{
      const date = new Date();
      this.updateTime(date);
    },1000)
    this.day = this.days[this.date.getDay()]
  }
  updateTime(date: Date) {
    const hours = date.getHours();
    this.hour = hours < 10 ? '0'+ hours : hours;

    const minutes = date.getMinutes();
    this.minute = minutes < 10 ? '0'+ minutes : minutes.toString();

    const seconds = date.getSeconds();
    this.second = seconds < 10 ? '0'+ seconds : seconds.toString();
  }

}