package com.mihalsky.kitchen.service;

import static com.mihalsky.kitchen.constant.EmailConstant.CC_EMAIL;
import static com.mihalsky.kitchen.constant.EmailConstant.DEFAULT_PORT;
import static com.mihalsky.kitchen.constant.EmailConstant.EMAIL_SUBJECT;
import static com.mihalsky.kitchen.constant.EmailConstant.EMAIL_SUBJECT_FOOD;
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

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mihalsky.kitchen.domain.User;
import com.mihalsky.kitchen.domain.food.Food;
import com.sun.mail.smtp.SMTPTransport;

@Service
public class EmailFoodService {
  private UserService userService;
  
  @Autowired
  public EmailFoodService(UserService userService) {
    this.userService = userService;
  }

  public void sendMessageAboutNewFoodToUsers(Food food) throws MessagingException {
    List<User> users = this.userService.getUsers();
    
    for(User user: users) {
    Message message = createEmail(user.getFirstName(),food,user.getEmail());
    SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
    smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
    smtpTransport.sendMessage(message, message.getAllRecipients());
    smtpTransport.close();
    }
}

private Message createEmail(String firstName,Food food, String email) throws MessagingException {
    Message message = new MimeMessage(getEmailSession());
    message.setFrom(new InternetAddress(FROM_EMAIL));
    message.setRecipients(TO, InternetAddress.parse(email, false));
    message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
    message.setSubject(EMAIL_SUBJECT_FOOD);
    message.setText("Здравствуйте " + firstName +
        ". \n \n В меню появилось новая позиция!"+
        "\n \n Название: "+ food.getName() + 
        "\n \n Состав: "+ food.getDescription() +
        "\n \n Каллории: "+ food.getCalories() +
        "\n \n Белки: "+ food.getProteins() +
        "\n \n Жиры: "+ food.getFats() +
        "\n \n Углеводы: "+ food.getCarbs() +
        "\n \n Вес: "+ food.getWeight() +
        "\n \n Цена: "+ food.getPrice() + "\n \n Служба поддержки");
    message.setSentDate(new Date());
    message.saveChanges();
    return message;
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
