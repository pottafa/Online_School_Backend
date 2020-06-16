package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.LessonRepository;

@Service
public class LessonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonService.class);
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public Long createLesson(Lesson lesson, Course course) {
        boolean success = course.addLesson(lesson);
        Long id = null;
        if(!success) return id;
        try{
             id = lessonRepository.save(lesson).getId();
            LOGGER.info("Lesson with id {} was successfully created", id);
            Course updatedCourse = courseRepository.save(course);
        }catch (Exception e) {
            LOGGER.error("Lesson was not created", e);
        }
        return id;
    }
}
