package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.dto.LessonMenuItemDTO;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.LessonRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LessonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LessonService.class);

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private LessonRepository lessonRepository;


    public List<LessonMenuItemDTO> findLessonByCourse(Long courseId) {
      return lessonRepository.findByCourse_Id(courseId);

    }

    public Lesson findLessonById(long lessonId) {
        return lessonRepository.findById(lessonId).orElse(null);
    }

    @Transactional
    public Long createLesson(Long courseId, Lesson lesson) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed create lesson. Course with id {} not exist", courseId);
            return null;
        }
        if (lessonRepository.existsById(lesson.getId())) {
            LOGGER.error("Failed create lesson. Lesson with id {} already exist", lesson.getId());
            return null;
        }
        lesson.setCourse(course);
        Long lessonId =  lessonRepository.save(lesson).getId();
        LOGGER.info("Lesson with id {} was successfully created", lessonId);
        return lessonId;
    }


    @Transactional
    public boolean deleteLesson(Long lessonId) {
        if(lessonRepository.existsById(lessonId)) {
            lessonRepository.deleteById(lessonId);
            LOGGER.info("Lesson with id {} was successfully deleted", lessonId);
            return true;
        }
        LOGGER.error("Failed delete lesson with id {}", lessonId);
        return false;
    }

    @Transactional
    public Lesson updateLesson(Long lessonId ,Lesson lesson) {
        Lesson lessonFromDB = lessonRepository.findById(lessonId).orElse(null);
        if(lessonFromDB == null) {
            LOGGER.error("Failed update lesson. Lesson does not exist");
            return null;
        }
        lessonFromDB.setTitle(lesson.getTitle());
        lessonFromDB.setDescription(lesson.getDescription());
        Lesson updatedLesson = lessonRepository.save(lessonFromDB);
        LOGGER.info("Lesson with id {} was successfully updated", updatedLesson.getId());
        return lesson;
    }
}
