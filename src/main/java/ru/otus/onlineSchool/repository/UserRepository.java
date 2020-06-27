package ru.otus.onlineSchool.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.otus.onlineSchool.dto.UserMenuItemDTO;
import ru.otus.onlineSchool.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
    List<UserMenuItemDTO> findAllDTOBy();

    @Query(value = "select email from profiles p where p.user_id = :id", nativeQuery = true)
    String findUserEmail(Long id);


}
