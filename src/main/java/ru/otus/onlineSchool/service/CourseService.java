package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.dto.CourseMenuItemDTO;
import ru.otus.onlineSchool.dto.CourseStatisticsView;
import ru.otus.onlineSchool.dto.CourseView;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.repository.CourseRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    @Autowired
    private CourseRepository courseRepository;


    public Long createCourse(Course course) {
        if (courseRepository.existsById(course.getId())) {
            LOGGER.error("Failed create course. Course with id {} already exist", course.getId());
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

    public List<CourseMenuItemDTO> findAllCourses() {
        return courseRepository.findAllCoursesMenuDTOBy();
    }

    public Course findCourseById(long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.orElse(null);
    }

    public CourseView findCoursePageViewById(long courseId) {
        Optional<CourseView> course = courseRepository.findCoursePageViewById(courseId);
        return course.orElse(null);
    }

    public boolean deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            LOGGER.info("Course with id {} does not exist and cannot be deleted ", courseId);
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

    public List<CourseStatisticsView> getAllCoursesStatisticsByUsers() {
        return courseRepository.findAllCoursesStatisticsByUsers();
    }

}
