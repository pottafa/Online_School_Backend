package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.LessonMenuItemDTO;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.service.LessonService;

import java.util.List;


@RestController
public class LessonRestController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable("lessonId") long lessonId) {
        Lesson lesson = lessonService.findLessonById(lessonId);
        if (lesson != null) {
            return ResponseEntity.ok(lesson);
        }
        return ResponseEntity.ok(new ApiError("Failed get lesson"));
    }

    @GetMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonMenuItemDTO>> getLessons(@PathVariable("courseId") long courseId) {
        List<LessonMenuItemDTO> lessons = lessonService.findLessonByCourse(courseId);
        return ResponseEntity.ok(lessons);
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
    public ResponseEntity<?> deleteLesson(@PathVariable("lessonId") Long lessonId) {
        boolean isDeleted = lessonService.deleteLesson(lessonId);
        if (isDeleted) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete lesson"));
        }
    }

    @PutMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable("lessonId") Long lessonId, @RequestBody Lesson lesson) {
        Lesson updatedLesson = lessonService.updateLesson(lessonId, lesson);
        if (updatedLesson == null) {
            return ResponseEntity.ok(new ApiError("Failed update lesson"));
        }
        return ResponseEntity.ok(updatedLesson);
    }
}
