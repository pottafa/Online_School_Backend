package ru.otus.onlineSchool.controllers.rest;

import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.service.CourseService;

import java.util.List;

@RestController
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/api/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        boolean success = courseService.createCourse(course);
        if (success) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(null);
            //TODO: при ошибке надо также возвращать 200 ResponseEntity.ok(), но саму ошибку отправлять в JSON, см. тест createCourseErrorMessage()
        }
    }

    @GetMapping("/api/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/api/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/api/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable("id") long id, @RequestBody Course course) {
        courseService.updateCourse(course);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") long id) {
        Course course = courseService.findCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/courses/{id}/lessons")
    public ResponseEntity<?> getLessons() {
        // TODO: реализовать получение уроков по курсу
        throw new UnsupportedOperationException("not implemented");
    }
}
