package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;


    public Long createUser(User user) {
        User userFromDB = userRepository.findByLogin(user.getLogin()).orElse(null);
        if (userFromDB != null) return null;
        Long id = null;
        try {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            id = userRepository.save(user).getId();
            LOGGER.info("User with id {} was successfully created", id);
        } catch (Exception e) {
            LOGGER.error("User was not created", e);
        }
        return id;
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }


    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            LOGGER.info("User with id {} was successfully deleted", id);
        } catch (Exception e) {
            LOGGER.error("User with id {} was not deleted", id, e);
        }
    }

    public void updateUser(User user) {
        try {
            userRepository.save(user);
            LOGGER.info("User with id {} was successfully updated", user.getId());
        } catch (Exception e) {
            LOGGER.error("User was not updated", e);
        }
    }

}
