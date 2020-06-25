package ru.otus.onlineSchool.notification.message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailNotificationTemplate {
    private String subject;
    private String message;
    private Date date;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        this.date = formatter.parse(date);
    }
}
