package ru.otus.onlineSchool.dataBase.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.dataBase.entity.Course;
import ru.otus.onlineSchool.dataBase.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private CourseRepository courseRepository;

    public boolean createCourse(Course course) {
        Course courseFromDB = courseRepository.findByTitle(course.getTitle()).orElse(null);
        if (courseFromDB != null) {
            LOGGER.error("Course already exist");
            return false;
        }
        try{
           Long id = courseRepository.save(course).getId();
            LOGGER.info("Course with id {} was successfully created", id);
        }catch (Exception e) {
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

    public void deleteCourse(String title) {
        try{
      courseRepository.deleteByTitle(title);

            LOGGER.info("Course with title {} was successfully deleted", title);
        }catch (Exception e) {
            LOGGER.error("Course with title {} was not deleted",title, e);
        }
    }

    public Course updateCourse(Course course) {
        try{
          Course updatedCourse =  courseRepository.save(course);
            LOGGER.info("Course with title {} was successfully updated", updatedCourse.getTitle());
            return updatedCourse;
        }catch (Exception e) {
            LOGGER.error("Course was not updated", e);
        }
        return null;
    }
}
