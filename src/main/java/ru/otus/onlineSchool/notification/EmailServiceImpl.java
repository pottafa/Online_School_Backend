package ru.otus.onlineSchool.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;
import ru.otus.onlineSchool.service.CourseService;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;


@Service("EmailService")
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;


    public boolean sendSimpleMessage(String to, EmailNotificationTemplate emailNotificationTemplate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(emailNotificationTemplate.getSubject());
            message.setText(emailNotificationTemplate.getMessage());

            Date dateToSent = emailNotificationTemplate.getDate();
            if(dateToSent != null) {
                message.setSentDate(dateToSent);
            }
            emailSender.send(message);
        } catch (MailException exception) {
            LOGGER.error("Failed send message to {}", to, exception);
            return false;
        }
        return true;
    }


}
