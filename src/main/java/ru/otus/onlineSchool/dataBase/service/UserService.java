package ru.otus.onlineSchool.dataBase.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.controllers.UserRestController;
import ru.otus.onlineSchool.dataBase.entity.User;
import ru.otus.onlineSchool.dataBase.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long createUser(User user) {
        Long id = null;
        try{
             id = userRepository.save(user).getId();
             LOGGER.info("User with id {} was successfully created", id);
        }catch (Exception e) {
            LOGGER.error("User was not created");
        }
        return id;
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public void deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
            LOGGER.info("User with id {} was successfully deleted", id);
        }catch (Exception e) {
            LOGGER.error("User with id {} was not deleted",id);
        }
    }

    public void updateUser(User user) {
        try{
            userRepository.save(user);
            LOGGER.info("User with id {} was successfully updated", user.getId());
        }catch (Exception e) {
            LOGGER.error("User was not updated");
        }
    }

}
