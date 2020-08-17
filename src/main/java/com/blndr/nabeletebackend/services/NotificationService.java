package com.blndr.nabeletebackend.services;

import com.blndr.nabeletebackend.model.Holders.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendRegistrationConfirmationEmail(RegistrationRequest registrationRequest) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registrationRequest.getUser().getEmail());
        mailMessage.setFrom(senderEmail);
        mailMessage.setSubject("Please confirm your registration to NaBelete");
        mailMessage.setText("Please confirm your registration to NaBelete by clicking this link localhost:8080/confirm-registration/" + registrationRequest.getUuid());

        javaMailSender.send(mailMessage);
    }
}
