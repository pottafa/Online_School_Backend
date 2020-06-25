package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.LessonMenuItemDTO;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.service.LessonService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class LessonRestController {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable("lessonId") long lessonId) {
        Lesson lesson = lessonService.findLessonById(lessonId);
        if (lesson != null) {
            return ResponseEntity.ok(lesson);
        }
        return ResponseEntity.ok(new ApiError("Failed get lesson"));
    }

    @GetMapping("/api/courses/{id}/lessons")
    public ResponseEntity<?> getLessons(@PathVariable("id") long id) {
        List<Lesson> lessons = lessonService.findLessonByCourse(id);
        if (lessons != null) {
            List<LessonMenuItemDTO> lessonsDto = lessons.stream()
                    .map(lesson -> modelMapper.map(lesson, LessonMenuItemDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(lessons);
        } else {
            return ResponseEntity.ok(new ApiError("Failed get lessons"));
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
    public ResponseEntity<?> deleteLesson(@PathVariable("lessonId") Long lessonId) {
        boolean isDeleted = lessonService.deleteLesson(lessonId);
        if (isDeleted) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete lesson"));
        }
    }

    @PutMapping("/api/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable("lessonId") Long lessonId,
                                         @RequestBody LessonMenuItemDTO lesson) {
        Lesson lessonFromDB = lessonService.findLessonById(lessonId);
        if (lessonFromDB == null) {
            return ResponseEntity.ok(new ApiError("Failed update lesson"));
        }
        modelMapper.map(lesson, lessonFromDB);
        Lesson updatedLesson = lessonService.updateLesson(lessonFromDB);
        return ResponseEntity.ok(lessonFromDB);
    }
}
