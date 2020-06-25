package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.CourseMenuItemDTO;
import ru.otus.onlineSchool.dto.UserMenuItemDTO;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseRestController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private ModelMapper modelMapper;

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
    public ResponseEntity<List<CourseMenuItemDTO>> getAllCourses() {
        List<CourseMenuItemDTO> courses = courseService.findAllCourses().stream()
                .map(course -> modelMapper.map(course, CourseMenuItemDTO.class))
                .collect(Collectors.toList());
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
    public ResponseEntity<?> updateCourse(@PathVariable("courseId") Long courseId, @RequestBody CourseMenuItemDTO course) {
        Course courseFromDb = courseService.findCourseById(courseId);
        if (courseFromDb == null) {
            return ResponseEntity.ok(new ApiError("Failed update course"));
        }
        modelMapper.map(course, courseFromDb);
        Course updatedCourse = courseService.updateCourse(courseFromDb);
        return ResponseEntity.ok(updatedCourse);
    }


}
