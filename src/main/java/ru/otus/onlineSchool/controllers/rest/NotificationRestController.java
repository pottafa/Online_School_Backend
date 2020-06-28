package ru.otus.onlineSchool.controllers.rest;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.notification.job.EmailNotificationJob;
import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;
import ru.otus.onlineSchool.service.GroupService;
import ru.otus.onlineSchool.service.UserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class NotificationRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationRestController.class);
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    @Autowired
    private Scheduler scheduler;

    @PostMapping("/api/users/{id}/notifications")
    public ResponseEntity<?> notifyUser(@PathVariable("id") Long id, @RequestBody EmailNotificationTemplate emailNotificationTemplate) throws SchedulerException {
        Date date = parseStringDate(emailNotificationTemplate.getDate());
        if (date == null) {
            LOGGER.error("Failed set notification to user. Wrong date format");
            return ResponseEntity.ok(new ApiError("Failed notify user. Wrong date format"));
        }
        if (date.before(new Date())) {
            LOGGER.error("Failed set notification to user. Datetime must be after current time ");
            return ResponseEntity.ok(new ApiError("Failed notify user. Datetime must be after current time"));
        }
        String email = userService.findUserEmail(id);
        if (email == null) {
            LOGGER.error("Failed set notification to user. Email does not exist");
            return ResponseEntity.ok(new ApiError("Failed notify user. Email does not exist"));
        }
        JobDetail jobDetail = buildJobDetail(email, emailNotificationTemplate, EmailNotificationJob.class);
        Trigger trigger = buildJobTrigger(jobDetail, date);
        scheduler.scheduleJob(jobDetail, trigger);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/api/courses/{course_id}/groups/{group_id}/notifications")
    public ResponseEntity<?> notifyGroup(@PathVariable("course_id") Long course_id, @PathVariable("group_id") Long group_id, @RequestBody EmailNotificationTemplate emailNotificationTemplate) throws SchedulerException {
        Group group = groupService.findGroupById(group_id);
        if (group == null) {
            return ResponseEntity.ok(new ApiError("Failed notify group. Group does not exist"));
        }
        List<User> users = group.getUsers();
        Date date = parseStringDate(emailNotificationTemplate.getDate());
        if (date == null) {
            LOGGER.error("Failed set notification to user. Wrong date format");
            return ResponseEntity.ok(new ApiError("Failed notify user. Wrong date format"));
        }
        if (date.before(new Date())) {
            LOGGER.error("Failed set notification to user. Datetime must be after current time ");
            return ResponseEntity.ok(new ApiError("Failed notify user. Datetime must be after current time"));
        }
        for (User user : users) {
            String email = user.getProfile().getEmail();
            JobDetail jobDetail = buildJobDetail(email, emailNotificationTemplate, EmailNotificationJob.class);
            Trigger trigger = buildJobTrigger(jobDetail, date);
            scheduler.scheduleJob(jobDetail, trigger);
        }
        return ResponseEntity.ok(null);
    }


    private JobDetail buildJobDetail(String toEmail, EmailNotificationTemplate notificationTemplate, Class<? extends Job> type) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("toEmail", toEmail);
        jobDataMap.put("subject", notificationTemplate.getSubject());
        jobDataMap.put("body", notificationTemplate.getMessage());

        return JobBuilder.newJob(type)
                .withIdentity(UUID.randomUUID().toString(), "email-notification-job")
                .withDescription("Send email notification")
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    private Date parseStringDate(String dateToParse) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        try {
            return formatter.parse(dateToParse);
        } catch (ParseException e) {
            LOGGER.error("Wrong date format", e);
            return null;
        }
    }


}
