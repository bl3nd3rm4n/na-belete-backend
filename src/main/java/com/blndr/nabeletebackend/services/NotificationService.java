package com.blndr.nabeletebackend.services;

import com.blndr.nabeletebackend.model.RegistrationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendRegistrationConfirmationEmail(RegistrationCode registrationCode) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registrationCode.getUser().getUsername());
        mailMessage.setFrom(senderEmail);
        mailMessage.setSubject("Please confirm your registration to NaBelete");
        mailMessage.setText("Please confirm your registration to NaBelete by clicking this link localhost:8080/confirm-registration/" + registrationCode.getUuid());

        javaMailSender.send(mailMessage);
    }
}
