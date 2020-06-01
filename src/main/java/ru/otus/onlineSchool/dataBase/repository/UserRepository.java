package ru.otus.onlineSchool.dataBase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.onlineSchool.dataBase.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
