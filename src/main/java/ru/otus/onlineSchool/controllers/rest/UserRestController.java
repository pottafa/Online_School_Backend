package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;

import ru.otus.onlineSchool.dto.UserMenuItemDTO;


import ru.otus.onlineSchool.entity.User;

import ru.otus.onlineSchool.service.UserService;

import java.util.List;


@RestController
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.ok(new ApiError("Failed get user"));
        }
    }

    @PostMapping("/api/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Long id = userService.createUser(user);
        if (id != null) return ResponseEntity.ok(id);
        else return ResponseEntity.ok(new ApiError("Failed create user"));
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> getUsers() {
        List<UserMenuItemDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete user"));
        }
    }

    @PutMapping("/api/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @RequestBody UserMenuItemDTO user) {
        User userFromDb = userService.findUserById(userId);
        if (userFromDb == null) {
            return ResponseEntity.ok(new ApiError("Failed update user"));
        }
        modelMapper.map(user, userFromDb);
        User updatedUser = userService.updateUser(userFromDb);
        return ResponseEntity.ok(updatedUser);
    }

}
