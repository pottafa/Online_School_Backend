package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.dto.UserMenuItemDTO;

import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.UserRepository;

import java.util.List;


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

    public List<UserMenuItemDTO> findAllUsers() {
       return userRepository.findAllDTOBy();
    }

    public String findUserEmail(Long userId) {
        return userRepository.findUserEmail(userId);
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
