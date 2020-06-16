package ru.otus.onlineSchool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.onlineSchool.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
