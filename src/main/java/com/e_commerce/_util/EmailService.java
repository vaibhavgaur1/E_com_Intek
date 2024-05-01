package com.e_commerce._util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public String sendEmailWithAttachment(byte[] pdfBytes) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("recipient@example.com");
        helper.setSubject("PDF Attachment");
        helper.setText("Please find the attached PDF.");
        ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);

        helper.addAttachment("document.pdf", pdfAttachment);

        emailSender.send(message);

        return "Email sent!";
    }

    public boolean sendTokenEmail(String email) {
        return true;
    }

    public void sendOtpMail(Long otp, String email) throws MessagingException {
        SimpleMailMessage message= new SimpleMailMessage();
//        MimeMessage message = emailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        message.setTo(email);
        message.setSubject("otp for verification");
        message.setText("valid otp" +  otp);
        System.out.println("sending");
        emailSender.send(message);
        System.out.println("sent");

    }

    public void sendWelcomeMail(Long otp, String email) {

    }
}

