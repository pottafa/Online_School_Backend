package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.entity.Role;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.entity.UserProfile;
import ru.otus.onlineSchool.service.UserService;

import java.util.Collections;
import java.util.List;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;


    @PostMapping("/api/saveUser")
    public ResponseEntity<?> saveUser(@RequestBody UserAndProfile json) {
        User user = json.user;
        user.setProfile(json.userProfile);
        Long id = userService.createUser(user);
        if (id != null) return ResponseEntity.ok(id);
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        Long id = userService.createUser(user);
        if (id != null) return ResponseEntity.ok(id);
        else return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/api/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/api/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/api/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UserAndProfile json) {
        User user = json.user;
        user.setProfile(json.userProfile);
        userService.updateUser(user);
        return ResponseEntity.ok(null);
    }

    static class UserAndProfile {
        public UserProfile userProfile;
        public User user;
    }

}
