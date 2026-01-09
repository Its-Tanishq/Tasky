package com.tasky.tasky.service;

import com.tasky.tasky.repo.OrganizationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OrganizationRepo organizationRepo;

    // For Simple Text Emails
    public void sendMail(String to, String name, String subject, String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText("Hi " + name + ",\n\n" + text + "\n\nRegards,\nTasky Team");
            javaMailSender.send(mailMessage);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    // For HTML Emails
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage());
        }
    }
}
