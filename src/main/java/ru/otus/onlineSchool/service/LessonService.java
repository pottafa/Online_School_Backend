package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.repository.CourseRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LessonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LessonService.class);

    @Autowired
    private CourseRepository courseRepository;

    public Lesson findLessonById(Course course, long lessonId) {
        return course.getLessons().stream()
                .filter(les -> les.getId() == lessonId)
                .peek(gr -> LOGGER.info("Lesson with id {} was retrieved successfully", lessonId))
                .findAny()
                .orElse(null);
    }

    @Transactional
    public Long createLesson(Long id, Lesson lesson) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            LOGGER.error("Failed create lesson. Course with id {} not exist", id);
            return null;
        }
        boolean isAdded = course.addLesson(lesson);
        if (!isAdded) {
            LOGGER.error("Error with adding lesson to course with id {}", id);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        List<Lesson> lessons = updatedCourse.getLessons();
        Long lessonId = lessons.get(lessons.size() - 1).getId();
        LOGGER.info("Lesson with id {} was successfully created", lessonId);
        return lessonId;
    }

    @Transactional
    public Course deleteLesson(Long courseId, Long lessonId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed delete lesson. Course with id {} not exist", courseId);
            return null;
        }
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        boolean isDeleted = course.removeLesson(lesson);
        if (!isDeleted) {
            LOGGER.error("Error with deleting lesson in Course with id {}", courseId);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Lesson with id {} was successfully deleted", lessonId);
        return updatedCourse;
    }

    @Transactional
    public Course updateLesson(Long courseId, Long lessonId, Lesson lesson) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed update lesson. Course with id {} not exist", courseId);
            return null;
        }
        Lesson courseLesson = findLessonById(course, lessonId);
        if (courseLesson == null) {
            LOGGER.error("Failed retrieve lesson from Course with id {}", courseId);
            return null;
        }
        courseLesson.setTitle(lesson.getTitle());
        courseLesson.setDescription(lesson.getDescription());
        boolean isUpdated = course.updateLesson(courseLesson);
        if (!isUpdated) {
            LOGGER.error("Failed update lesson in Course with id {}", courseId);
            return null;
        }
Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Lesson with id {} was successfully updated", lessonId);
        return updatedCourse;
    }
}
