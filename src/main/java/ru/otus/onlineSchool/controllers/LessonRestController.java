package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.dataBase.entity.Course;
import ru.otus.onlineSchool.dataBase.entity.Lesson;
import ru.otus.onlineSchool.dataBase.service.CourseService;
import ru.otus.onlineSchool.dataBase.service.LessonService;


@RestController
public class LessonRestController {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private CourseService courseService;

    @PostMapping("/api/createLesson")
    public ResponseEntity<?> createLesson(@RequestBody CourseAndLesson json) {
        Lesson lesson = json.lesson;
        Course course = courseService.findCourse(json.courseTitle);

        Long id = lessonService.createLesson(lesson, course);
        if (id != null) return ResponseEntity.ok(id);
        return ResponseEntity.badRequest().body("The lesson already exist");
    }

    @PostMapping("/api/getCourseLessons")
    public ResponseEntity<?> getCourseLessons(@RequestBody TextNode title) {
        Course course = courseService.findCourse(title.asText());
        return ResponseEntity.ok(course.getLessons());
    }

    @DeleteMapping("/api/deleteLesson")
    public ResponseEntity<?> deleteLesson(@RequestBody CourseAndLesson json) {
        Lesson lesson = json.lesson;
        Course course = courseService.findCourse(json.courseTitle);
        boolean success = course.removeLesson(lesson);
        if (success) {
            courseService.updateCourse(course);
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().body("There is no such lesson");
    }

    @PutMapping("/api/updateLesson")
    public ResponseEntity<?> updateLesson(@RequestBody CourseAndLesson json) {
        Lesson lesson = json.lesson;
        Course course = courseService.findCourse(json.courseTitle);
      boolean success = course.updateLesson(lesson);
      if(!success) return ResponseEntity.badRequest().body(null);
        courseService.updateCourse(course);
        return ResponseEntity.ok(null);
    }


    static class CourseAndLesson {
        public String courseTitle;
        public Lesson lesson;
    }
}
