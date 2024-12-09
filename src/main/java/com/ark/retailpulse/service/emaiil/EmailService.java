package com.ark.retailpulse.service.emaiil;

import com.ark.retailpulse.model.Order;
import com.ark.retailpulse.model.Otp;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.OtpRepository;
import com.ark.retailpulse.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final OtpRepository otpRepository;


    @Value("spring.mail.username")
    private String fromEmail;

    public void sendOrderConfirmation(Order order){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order confirmation");
        message.setText("Your order has been confirmed. Order ID " + order.getId());
//        mailSender.send(message);
        logger.info("Order confirmation sent to " + order.getUser().getEmail());
    }

    public void sendEmailConfirmationCode(User user){

        Otp otp = otpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException(" user not found for sending otp"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Confirm your email");
        message.setText("Please confirm your email by entering this security code " + otp.getEmailOtpCode());
//        todo: uncomment below line while demo
//        mailSender.send(message);
        logger.info("Confirm your email sent to " + user.getEmail());

    }

    public void sendOrderFailureNotification(Order order) { //todo: order confirmation
        // Logic for sending a failure notification email
    }



}
