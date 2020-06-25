package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.UserMenuItemDTO;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.notification.EmailService;
import ru.otus.onlineSchool.notification.message.EmailNotificationTemplate;
import ru.otus.onlineSchool.service.CourseService;
import ru.otus.onlineSchool.service.GroupService;
import ru.otus.onlineSchool.service.UserService;

@RestController
public class NotificationRestController {
    @Autowired
   private EmailService emailService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    @PostMapping("/api/users/{id}/notifications")
    public ResponseEntity<?> notifyUser(@PathVariable("id") Long id, @RequestBody EmailNotificationTemplate emailNotificationTemplate) {
        User user = userService.findUserById(id);
        if (user != null) {
            String email = user.getEmail();
            if(emailService.sendSimpleMessage(email,emailNotificationTemplate)) {
                return ResponseEntity.ok(null);
            }
        }
        return ResponseEntity.ok(new ApiError("Failed notify user"));
    }


}
