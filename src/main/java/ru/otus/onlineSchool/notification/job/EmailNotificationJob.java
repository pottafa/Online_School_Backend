package ru.otus.onlineSchool.notification.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.otus.onlineSchool.notification.EmailService;

public class EmailNotificationJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationJob.class);

    @Autowired
    private EmailService emailService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        LOGGER.info("Notification: job with key {} is executing", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String toEmail = jobDataMap.getString("toEmail");

        emailService.sendSimpleMessage(toEmail, subject, body);
    }
}
