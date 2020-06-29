package ru.otus.onlineSchool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.otus.onlineSchool.service.CourseService;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @RequestMapping(value = "/courses/{courseId}", method = RequestMethod.GET)
    public String getUser(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.findCoursePageViewById(courseId));
        return "course";
    }

}
