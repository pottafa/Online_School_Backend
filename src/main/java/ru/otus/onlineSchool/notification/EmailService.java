package ru.otus.onlineSchool.notification;

import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;

public interface EmailService {
    boolean sendMessage(String toEmail, String subject, String body);

}
