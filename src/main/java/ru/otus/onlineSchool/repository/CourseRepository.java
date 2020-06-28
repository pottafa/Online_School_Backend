package ru.otus.onlineSchool.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.dto.CourseStatisticsDTO;
import ru.otus.onlineSchool.dto.CourseStatisticsView;
import ru.otus.onlineSchool.entity.Course;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findByTitle(String title);
    <T> List<T> findAllBy(Class<T> type);

@Query( value = "SELECT courses.title as title, COUNT(*) as count" +
        " FROM USERS_GROUPS" +
        " INNER JOIN GROUPS USING (group_id)" +
        " INNER JOIN COURSES USING (course_id)" +
        " GROUP BY courses.title", nativeQuery=true)
    List<CourseStatisticsView> getCourseStatistics();
}
