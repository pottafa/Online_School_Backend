package ru.otus.onlineSchool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.dto.LessonMenuItemDTO;
import ru.otus.onlineSchool.entity.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {
   List<LessonMenuItemDTO> findByCourse_Id(Long id);
}
