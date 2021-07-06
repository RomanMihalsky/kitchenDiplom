package com.mihalsky.kitchen.service.impl;

import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mihalsky.kitchen.domain.Order;
import com.mihalsky.kitchen.domain.User;
import com.mihalsky.kitchen.domain.seat.SeatDateOrder;
import com.mihalsky.kitchen.repository.OrderRepository;
import com.mihalsky.kitchen.service.EmailBuyService;
import com.mihalsky.kitchen.service.UserService;

@Service
@Transactional
public class OrderService {
  
  private OrderRepository orderRepository;
  private DateService dateService;
  private SeatService seatService;
  private UserService userService;
  private EmailBuyService emailBuyService;

  @Autowired
  public OrderService(OrderRepository orderRepository,DateService dateService,
      SeatService seatService,UserService userService,EmailBuyService emailBuyService) {
    this.orderRepository = orderRepository;
    this.dateService = dateService;
    this.seatService = seatService;
    this.userService = userService;
    this.emailBuyService = emailBuyService;
  }
  
  public List<Order> getAll(){
    return this.orderRepository.findAll();
  }
  
  public Order addOrder(Order order) throws MessagingException {
    SeatDateOrder dateOrder = this.dateService.serchByDate(order.getDateOrder());
    User user = userService.findUserByUsername(order.getUser().getUsername());
    order.setUser(user);
    if(!(order.getSeat()==null)) {
    dateOrder.addSeat(this.seatService.searchBySeatId(order.getSeat().getSeatId()));
    dateService.updateDate(dateOrder);
    emailBuyService.sendMessageEmail(order, user.getFirstName(), user.getEmail());
    return this.orderRepository.save(order);
    }
    
    order.setSeat(this.seatService.findById(85L));
    emailBuyService.sendMessageEmail(order, user.getFirstName(), user.getEmail());
    return this.orderRepository.save(order);
  }
  
  private String generateOrderId() {
    return RandomStringUtils.randomNumeric(10);
  }
}
