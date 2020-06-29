package ru.otus.onlineSchool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.onlineSchool.dto.CourseStatisticsView;
import ru.otus.onlineSchool.service.CourseService;

import java.util.List;

@Controller
public class ReportController {
    private static final String USERS_COUNT_PAGE = "users-count-report";

    @Autowired
    private CourseService courseService;

    @GetMapping("/admin-panel/statistics/courses")
    protected String courseUsersCount(Model model) {
        List<CourseStatisticsView> courses = courseService.getAllCoursesStatisticsByUsers();
        model.addAttribute("coursesDTO", courses);
        return USERS_COUNT_PAGE;
    }
}
