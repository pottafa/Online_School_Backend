package ru.otus.onlineSchool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.entity.Lesson;

import java.util.Optional;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

    Optional<Lesson> findByTitle(String title);

    Lesson save(Lesson lesson);
}
