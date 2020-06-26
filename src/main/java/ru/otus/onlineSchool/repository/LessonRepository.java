package ru.otus.onlineSchool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {
   <T> List<T> findByCourse_Id(Long id, Class<T> type);
}
