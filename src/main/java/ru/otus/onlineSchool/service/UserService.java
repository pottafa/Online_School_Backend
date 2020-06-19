package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;


    public User findUserById(long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            LOGGER.error("User with id {} not found", id);
            return null;
        }
        return user;
    }

    public Long createUser(User user) {
        User userFromDB = userRepository.findById(user.getId()).orElse(null);
        if (userFromDB != null) return null;
        LOGGER.error("Failed create user. User with id {} already exist", user.getId());
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


    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            LOGGER.info("User with id {} is already exist and cannot be deleted ", id);
            return false;
        }
        userRepository.deleteById(id);
        LOGGER.info("User with id {} was successfully deleted", id);
        return true;
    }

    @Transactional
    public User updateUser(User user) {
        try {
            if(!userRepository.existsById(user.getId())) {
                LOGGER.error("Failed update user. User with id {} not found", user.getId());
                return null;
            }
           User updatedUser = userRepository.save(user);
            LOGGER.info("User with id {} was successfully updated", user.getId());
            return updatedUser;
        } catch (Exception e) {
            LOGGER.error("User was not updated", e);
        }
        return null;
    }

}
