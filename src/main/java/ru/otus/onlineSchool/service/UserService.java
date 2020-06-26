package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.dto.UserMenuView;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
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


    public User findUserById(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            LOGGER.error("User with id {} not found", userId);
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

    public List<UserMenuView> findAllUsers() {
       return userRepository.findAllBy();
    }


    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            LOGGER.info("User with id {} is already exist and cannot be deleted ", userId);
            return false;
        }
        userRepository.deleteById(userId);
        LOGGER.info("User with id {} was successfully deleted", userId);
        return true;
    }

    public User updateUser(User user) {
       User updatedUser = userRepository.save(user);
        LOGGER.info("User with id {} was successfully updated", updatedUser.getId());
        return updatedUser;
    }

}
