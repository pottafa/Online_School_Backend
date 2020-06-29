package ru.otus.onlineSchool.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.dto.*;
import ru.otus.onlineSchool.entity.Course;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<CourseView> findCoursePageViewById(Long id);

    List<CourseMenuItemDTO> findAllCoursesMenuDTOBy();

@Query( value = "SELECT courses.title as title, COUNT(*) as count" +
        " FROM USERS_GROUPS" +
        " INNER JOIN GROUPS USING (group_id)" +
        " INNER JOIN COURSES USING (course_id)" +
        " GROUP BY courses.title", nativeQuery=true)
    List<CourseStatisticsView> findAllCoursesStatisticsByUsers();
}
