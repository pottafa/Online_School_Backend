package ru.otus.onlineSchool.dataBase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.onlineSchool.dataBase.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
