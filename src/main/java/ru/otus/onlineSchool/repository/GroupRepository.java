package ru.otus.onlineSchool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    List<Group> findByCourse_Id(Long id);
}
