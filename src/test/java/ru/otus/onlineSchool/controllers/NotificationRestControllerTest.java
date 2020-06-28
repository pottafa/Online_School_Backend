package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NotificationRestControllerTest {
    @TestConfiguration
    static class CourseRestControllerTestContextConfiguration {
        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Scheduler scheduler;

    @Test
    void sendNotificationToUserSuccess() throws Exception {
        String dateToSend = "2020-12-20T00:12";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date expectedDate = formatter.parse(dateToSend);
        int userId = 116;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();

        notificationTemplate.setSubject("Test subject");
        notificationTemplate.setMessage("Test");
        notificationTemplate.setDate(dateToSend);

        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/users/" + userId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk());

        JobDetail jobDetail = null;
        List<? extends Trigger> triggersOfJob = null;

        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                 jobDetail = scheduler.getJobDetail(jobKey);
                 triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            }
        }
        assertThat(jobDetail).isNotNull();
        assertThat(triggersOfJob.size()).isEqualTo(1);

        Trigger trigger = triggersOfJob.get(0);
        assertThat(trigger.getNextFireTime()).isEqualTo(expectedDate);

        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String toEmail = jobDataMap.getString("toEmail");

        assertThat(subject).isEqualTo("Test subject");
        assertThat(body).isEqualTo("Test");
        assertThat(toEmail).isEqualTo("pottafa.spam@gmail.com");

    }

    @Test
    void sendNotificationToUserWrongDateFormatErrorMessage() throws Exception {
        ApiError errorMessage = new ApiError("Failed notify user. Wrong date format");
        String expectedResponse = new ObjectMapper().writeValueAsString(errorMessage);

        String incorrectDataFormat = "2020-12-20";
        int userId = 116;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();

        notificationTemplate.setSubject("Test subject");
        notificationTemplate.setMessage("Test");
        notificationTemplate.setDate(incorrectDataFormat);

        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/users/" + userId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    void sendNotificationToUserBeforeCurrentDatetimeErrorMessage() throws Exception {
        ApiError errorMessage = new ApiError("Failed notify user. Datetime must be after current time");
        String expectedResponse = new ObjectMapper().writeValueAsString(errorMessage);

        String beforeCurrentTimeDate = "2000-12-20T00:12";
        int userId = 116;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();

        notificationTemplate.setSubject("Test subject");
        notificationTemplate.setMessage("Test");
        notificationTemplate.setDate(beforeCurrentTimeDate);

        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/users/" + userId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    void sendNotificationToUserEmailNotExistErrorMessage() throws Exception {
        ApiError errorMessage = new ApiError("Failed notify user. Email does not exist");
        String expectedResponse = new ObjectMapper().writeValueAsString(errorMessage);

        String beforeCurrentTimeDate = "2020-12-20T00:12";
        int userId = 2222;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();

        notificationTemplate.setSubject("Test subject");
        notificationTemplate.setMessage("Test");
        notificationTemplate.setDate(beforeCurrentTimeDate);

        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/users/" + userId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }


    @Test
    void sendNotificationToGroupSuccess() throws Exception {
        String dateToSend = "2020-12-20T00:12";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date expectedDate = formatter.parse(dateToSend);
        int courseId = 103;
        int groupId = 114;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();

        notificationTemplate.setSubject("Test subject");
        notificationTemplate.setMessage("Test");
        notificationTemplate.setDate(dateToSend);

        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/courses/" + courseId + "/groups/" + groupId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk());

       List<JobDetail> jobDetails = new ArrayList<>();
       List<List<? extends Trigger>> triggersOfJobs = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobDetails.add(scheduler.getJobDetail(jobKey));
                triggersOfJobs.add(scheduler.getTriggersOfJob(jobKey));
            }
        }
        assertThat(jobDetails.size()).isEqualTo(2);
        assertThat(triggersOfJobs.size()).isEqualTo(2);

        Trigger trigger = triggersOfJobs.get(1).get(0);
        assertThat(trigger.getNextFireTime()).isEqualTo(expectedDate);

        JobDataMap jobDataMap = jobDetails.get(1).getJobDataMap();

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");

        assertThat(subject).isEqualTo("Test subject");
        assertThat(body).isEqualTo("Test");

    }


    @Test
    void sendNotificationToGroupErrorMessage() throws Exception {
        ApiError errorMessage = new ApiError("Failed notify group. Group does not exist");
        String expectedResponse = new ObjectMapper().writeValueAsString(errorMessage);

        String dateToSend = "2020-12-20T00:12";
        int courseId = 103;
        int groupId = 2222;

        EmailNotificationTemplate notificationTemplate = new EmailNotificationTemplate();
        String notificationJson = new ObjectMapper().writeValueAsString(notificationTemplate);

        mvc.perform(post("/api/courses/" + courseId + "/groups/" + groupId + "/notifications")
                .contentType("application/json")
                .content(notificationJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }
}
