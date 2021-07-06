package com.mihalsky.kitchen.resource;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mihalsky.kitchen.domain.HttpResponse;
import com.mihalsky.kitchen.domain.Order;
import com.mihalsky.kitchen.service.impl.OrderService;

@RestController
@RequestMapping(path = {"/order"})
public class OrderResource {
  private OrderService orderService;
  
  @Autowired
  public OrderResource(OrderService orderService) {
    this.orderService = orderService;
  }
  
  @GetMapping("/all")
  public ResponseEntity<List<Order>> getAllOrders(){
    List<Order> orders =  this.orderService.getAll();
    return new ResponseEntity<List<Order>>(orders,HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<HttpResponse> addOrder(@RequestBody Order order) throws MessagingException {
    this.orderService.addOrder(order);
    return response(HttpStatus.OK,"Добавление прошло успешно");
  }
  
  private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
            message), httpStatus);
  }
}
