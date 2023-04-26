package org.mini_amazon.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class EmailService {

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Resource
  private JavaMailSender javaMailSender;

  @Async
  public void sendEmail(String toEmail, String subject, String body) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromEmail);
    msg.setTo(toEmail);
    msg.setSubject(subject);
    msg.setText(body);

    System.out.println(msg);

    javaMailSender.send(msg);
    System.out.println("Email sent to " + toEmail + " successfully");
  }
}
