package ru.otus.onlineSchool.notification;

import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;

public interface EmailService {
    boolean sendSimpleMessage(String to,
                           EmailNotificationTemplate emailNotificationTemplate);

}
