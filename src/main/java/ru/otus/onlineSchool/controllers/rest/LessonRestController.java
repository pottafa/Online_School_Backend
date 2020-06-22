package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.service.CourseService;
import ru.otus.onlineSchool.service.LessonService;


@RestController
public class LessonRestController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private LessonService lessonService;

    @GetMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable("courseId") long courseId, @PathVariable("lessonId") long lessonId) {
        Course course = courseService.findCourseById(courseId);
        if (course != null) {
            Lesson lesson = lessonService.findLessonById(course, lessonId);
            if (lesson != null) {
                return ResponseEntity.ok(lesson);
            }
        }
        return ResponseEntity.ok(new ApiError("Failed get lesson"));
    }

    @GetMapping("/api/courses/{id}/lessons")
    public ResponseEntity<?> getLessons(@PathVariable("id") long id) {
        Course course = courseService.findCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(course.getLessons());
        } else {
            return ResponseEntity.ok(new ApiError("Failed get lessons from course"));
        }
    }

    @PostMapping("/api/courses/{id}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable("id") Long courseId, @RequestBody Lesson lesson) {
        Long lessonId = lessonService.createLesson(courseId, lesson);
        if (lessonId != null) {
            return ResponseEntity.ok(lessonId);
        } else {
            return ResponseEntity.ok(new ApiError("Failed create lesson"));
        }
    }

    @DeleteMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable("courseId") Long courseId, @PathVariable("lessonId") Long lessonId) {
        Course updatedCourse = lessonService.deleteLesson(courseId, lessonId);
        if (updatedCourse != null) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete lesson"));
        }
    }

    @PutMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable("courseId") Long courseId, @PathVariable("lessonId") Long lessonId,
                                          @RequestBody Lesson lesson) {
        Lesson updatedLesson = lessonService.updateLesson(courseId, lessonId, lesson);
        if (updatedLesson == null) {
            return ResponseEntity.ok(new ApiError("Failed update lesson"));
        }
        return ResponseEntity.ok(updatedLesson);
    }
}
