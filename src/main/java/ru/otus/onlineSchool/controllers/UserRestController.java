package ru.otus.onlineSchool.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.dataBase.entity.User;
import ru.otus.onlineSchool.dataBase.service.UserService;

import java.util.List;

@RestController
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/saveUser")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
      Long id = userService.createUser(user);
      if(id != null) return ResponseEntity.ok(id);
      else  return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/api/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAllUsers() ;
        return ResponseEntity.ok(users);
    }

    @PostMapping("/api/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody Long id) {
        userService.deleteUser(id); ;

        return ResponseEntity.ok(null);
    }

    @PostMapping("/api/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        userService.updateUser(user); ;

        return ResponseEntity.ok(null);
    }

}
