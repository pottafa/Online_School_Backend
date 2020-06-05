package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.dataBase.entity.Course;
import ru.otus.onlineSchool.dataBase.service.CourseService;

import java.util.List;

@RestController
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/api/createCourse")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        boolean success = courseService.createCourse(course);
        if (success) return ResponseEntity.ok(success);
        else return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/api/getAllCourses")
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/api/deleteCourse")
    public ResponseEntity<?> deleteCourse(@RequestBody TextNode title) {
        courseService.deleteCourse(title.asText());
        return ResponseEntity.ok(null);
    }


    @PutMapping("/api/updateCourse")
    public ResponseEntity<?> updateCourse(@RequestBody Course course) {
        courseService.updateCourse(course);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/getCourse")
    public ResponseEntity<?> getCourse() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }


}
