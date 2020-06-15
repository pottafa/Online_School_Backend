package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    @Autowired
    private CourseRepository courseRepository;

    public boolean createCourse(Course course) {
        Course courseFromDB = courseRepository.findByTitle(course.getTitle()).orElse(null);
        if (courseFromDB != null) {
            LOGGER.error("Course already exist");
            return false;
        }
        try {
            Long id = courseRepository.save(course).getId();
            LOGGER.info("Course with id {} was successfully created", id);
        } catch (Exception e) {
            LOGGER.error("Course was not created", e);
        }
        return true;
    }

    public List<Course> findAllCourses() {
        List<Course> courses = new ArrayList<>();
        courseRepository.findAll().forEach(courses::add);
        return courses;
    }

    public Course findCourse(String title) {
        Optional<Course> course = courseRepository.findByTitle(title);
        return course.orElse(null);
    }

    public Course findCourseById(long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    //Было так -- public void deleteCourse(String title) {
    // удалять по title совсем плохо, надо по id
    public void deleteCourse(Long id) {
        try {
            courseRepository.deleteById(id);
            LOGGER.info("Course with id {} was successfully deleted", id);
        } catch (Exception e) {
            LOGGER.error("Course with id {} was not deleted", id, e);
        }
    }

    public Course updateCourse(Course course) {
        try {
            Course updatedCourse = courseRepository.save(course);
            LOGGER.info("Course with title {} was successfully updated", updatedCourse.getTitle());
            return updatedCourse;
        } catch (Exception e) {
            LOGGER.error("Course was not updated", e);
        }
        return null;
    }
}
