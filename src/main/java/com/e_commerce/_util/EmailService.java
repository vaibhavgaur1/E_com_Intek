package com.e_commerce._util;

import com.e_commerce.Dto.MessageInput;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void sendAdminCredentials(String email, String username, String password) {
        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(email);

        message.setSubject("Credentials for IAF Store Admin ");

        message.setText("Hi, You have been assigned as admin for IAF Canteen"+
                "your username is : "+username+
                "your password is: "+ password
        );
        System.out.println("sending");
        emailSender.send(message);
        System.out.println("sent");
    }

    public void publishMessage(List<String> userEmailList, String publishedMessage) {

        String[] userEmails = (String[])userEmailList.toArray();
        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(userEmails);

        message.setSubject("Important Message from IAF Canteen ");

        message.setText(
                publishedMessage
        );
        System.out.println("sending");
        emailSender.send(message);
        System.out.println("sent");

    }
}

