package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.CourseMenuItemDTO;
import ru.otus.onlineSchool.dto.UserMenuItemDTO;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> createUser(@RequestBody UserMenuItemDTO userMenuItemDTO) {
        User user = modelMapper.map(userMenuItemDTO, User.class);
        Long id = userService.createUser(user);
        if (id != null) return ResponseEntity.ok(id);
        else return ResponseEntity.ok(new ApiError("Failed create user"));
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> getUsers() {
        List<UserMenuItemDTO> users = userService.findAllUsers().stream()
                .map(user -> modelMapper.map(user, UserMenuItemDTO.class))
                .collect(Collectors.toList());
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

    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.ok(new ApiError("Failed update user"));
        }
    }

}
