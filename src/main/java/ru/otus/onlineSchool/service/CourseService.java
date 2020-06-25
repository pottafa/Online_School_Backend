package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.CourseRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    @Autowired
    private CourseRepository courseRepository;


    public Long createCourse(Course course) {
        Course courseFromDB = courseRepository.findById(course.getId()).orElse(null);
        if (courseFromDB != null) {
            LOGGER.error("Course already exist");
            return null;
        }
        try {
            Long id = courseRepository.save(course).getId();
            LOGGER.info("Course with id {} was successfully created", id);
            return id;
        } catch (Exception e) {
            LOGGER.error("Course was not created", e);
        }
        return null;
    }

    public List<Course> findAllCourses() {
        List<Course> courses = new ArrayList<>();
        courseRepository.findAll().forEach(courses::add);
        return courses;
    }

    public Course findCourseById(long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.orElse(null);
    }

    public Course findCourseByTitle(String title) {
        Optional<Course> course = courseRepository.findByTitle(title);
        return course.orElse(null);
    }

    public boolean deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            LOGGER.info("Course with id {} is already exist and cannot be deleted ", courseId);
            return false;
        }
        courseRepository.deleteById(courseId);
        LOGGER.info("Course with id {} was successfully deleted", courseId);
        return true;
    }

    @Transactional
    public Course updateCourse(Long courseId, Course course) {
        Course courseFromDB = courseRepository.findById(courseId).orElse(null);
        if (courseFromDB == null) {
            LOGGER.info("Course with id {} was not updated", courseId);
            return null;
        }
        courseFromDB.setTitle(course.getTitle());
        courseFromDB.setDescription(course.getDescription());

        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Course with id {} was successfully updated", courseId);
        return updatedCourse;
    }

    @Transactional
    public Course updateCourse(Course course) {
        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Course with id {} was successfully updated", updatedCourse.getId());
        return updatedCourse;
    }

}
