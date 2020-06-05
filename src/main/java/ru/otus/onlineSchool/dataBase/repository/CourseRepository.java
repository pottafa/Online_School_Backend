package ru.otus.onlineSchool.dataBase.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.onlineSchool.dataBase.entity.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findByTitle(String title);
    @Transactional
    void deleteByTitle(String title);
}