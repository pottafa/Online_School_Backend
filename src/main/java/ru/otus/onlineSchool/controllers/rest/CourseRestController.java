package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.service.CourseService;

import java.util.List;

@RestController
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/api/courses/{id}")
    public ResponseEntity<?> getCourse(@PathVariable("id") long id) {
        Course course = courseService.findCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.ok(new ApiError("Failed get course"));
        }
    }

    @PostMapping("/api/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        Long courseId = courseService.createCourse(course);
        if (courseId != null) {
            return ResponseEntity.ok(courseId);
        } else {
            return ResponseEntity.ok(new ApiError("Failed create course"));
        }
    }

    @GetMapping("/api/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/api/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") long id) {
        boolean isDeleted = courseService.deleteCourse(id);
        if (isDeleted) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete course"));
        }
    }

    @PutMapping("/api/courses/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable("courseId") Long courseId, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(courseId, course);
        if (updatedCourse == null) {
            return ResponseEntity.ok(new ApiError("Failed update course"));
        }
        return ResponseEntity.ok(updatedCourse);
    }


}
