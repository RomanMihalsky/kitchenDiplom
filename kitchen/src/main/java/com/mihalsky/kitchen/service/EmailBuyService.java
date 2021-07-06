package com.mihalsky.kitchen.service;

import static com.mihalsky.kitchen.constant.EmailConstant.CC_EMAIL;
import static com.mihalsky.kitchen.constant.EmailConstant.DEFAULT_PORT;
import static com.mihalsky.kitchen.constant.EmailConstant.EMAIL_SUBJECT_BUY;
import static com.mihalsky.kitchen.constant.EmailConstant.FROM_EMAIL;
import static com.mihalsky.kitchen.constant.EmailConstant.GMAIL_SMTP_SERVER;
import static com.mihalsky.kitchen.constant.EmailConstant.PASSWORD;
import static com.mihalsky.kitchen.constant.EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL;
import static com.mihalsky.kitchen.constant.EmailConstant.SMTP_AUTH;
import static com.mihalsky.kitchen.constant.EmailConstant.SMTP_HOST;
import static com.mihalsky.kitchen.constant.EmailConstant.SMTP_PORT;
import static com.mihalsky.kitchen.constant.EmailConstant.SMTP_STARTTLS_ENABLE;
import static com.mihalsky.kitchen.constant.EmailConstant.SMTP_STARTTLS_REQUIRED;
import static com.mihalsky.kitchen.constant.EmailConstant.USERNAME;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.mihalsky.kitchen.domain.Order;
import com.mihalsky.kitchen.domain.food.Food;
import com.sun.mail.smtp.SMTPTransport;

@Service
public class EmailBuyService {
  
  public void sendMessageEmail(Order order,String firstName,String email) throws MessagingException {
    Message message = createEmail(order,firstName, email);
    SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
    smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
    smtpTransport.sendMessage(message, message.getAllRecipients());
    smtpTransport.close();
}

private Message createEmail(Order order,String firstName, String email) throws MessagingException {
    Message message = new MimeMessage(getEmailSession());
    message.setFrom(new InternetAddress(FROM_EMAIL));
    message.setRecipients(TO, InternetAddress.parse(email, false));
    message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
    message.setSubject(EMAIL_SUBJECT_BUY);
    if(order.getSeat().getId()==85L) {
    message.setText("Здравствуйте " + firstName +
        ". \n \n Ваш заказ!"+
        "\n \n Номер заказа: "+ order.getOrderId() + 
        "\n \n Время приготовления: "+ calculateTime(order.getWishFood()) 
         + "\n \n Служба поддержки");
    }else {
      message.setText("Здравствуйте " + firstName +
          ". \n \n Ваш заказ!"+
          "\n \n Номер заказа: "+ order.getOrderId() + 
          "\n \n Время приготовления: "+ calculateTime(order.getWishFood())+ 
          "\n \n Время ронирования: "+ order.getDateOrder().getHours() + 
          ":" + order.getDateOrder().getMinutes() +
          "\n \n Место: "+ order.getSeat().getSeatId()
           + "\n \n Служба поддержки");
    }
    message.setSentDate(new Date());
    message.saveChanges();
    return message;
}

private String calculateTime(List<Food> wishFood) {
  int max = wishFood.get(0).getTimeCook();
  for(Food food:wishFood) {
    if(food.getTimeCook()>max) {
      max = food.getTimeCook();
    }
  }
  return ""+max;
}

private Session getEmailSession() {
    Properties properties = System.getProperties();
    properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
    properties.put(SMTP_AUTH, true);
    properties.put(SMTP_PORT, DEFAULT_PORT);
    properties.put(SMTP_STARTTLS_ENABLE, true);
    properties.put(SMTP_STARTTLS_REQUIRED, true);
    return Session.getInstance(properties, null);
}
}
