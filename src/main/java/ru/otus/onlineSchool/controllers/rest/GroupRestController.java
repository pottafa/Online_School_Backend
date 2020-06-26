package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.GroupMenuItemDTO;
import ru.otus.onlineSchool.dto.GroupMenuView;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.service.GroupService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GroupRestController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/api/courses/{courseId}/groups/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable("groupId") long groupId) {
        Group group = groupService.findGroupById(groupId);
        if (group != null) {
                return ResponseEntity.ok(group);
        }
        return ResponseEntity.ok(new ApiError("Failed get group"));
    }

    @GetMapping("/api/courses/{id}/groups")
    public ResponseEntity<?> getGroups(@PathVariable("id") long id) {
        List<GroupMenuView> groups = groupService.findGroupByCourse(id, GroupMenuView.class);
        if (groups != null) {
            return ResponseEntity.ok(groups);
        } else {
            return ResponseEntity.ok(new ApiError("Failed get groups"));
        }
    }


    @PostMapping("/api/courses/{id}/groups")
    public ResponseEntity<?> createGroup(@PathVariable("id") Long courseId, @RequestBody Group group) {
        Long groupId = groupService.createGroup(courseId, group);
        if (groupId != null) {
            return ResponseEntity.ok(groupId);
        } else {
            return ResponseEntity.ok(new ApiError("Failed create group"));
        }
    }

    @DeleteMapping("/api/courses/{courseId}/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable("groupId") Long groupId) {
        boolean isDeleted = groupService.deleteGroup(groupId);
        if (isDeleted) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete group"));
        }
    }

    @PutMapping("/api/courses/{courseId}/groups/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("groupId") Long groupId,
                                         @RequestBody GroupMenuItemDTO group) {
        Group groupFromDB = groupService.findGroupById(groupId);
        if (groupFromDB == null) {
            return ResponseEntity.ok(new ApiError("Failed update group"));
        }
        modelMapper.map(group, groupFromDB);
        Group updatedGroup = groupService.updateGroup(groupFromDB);
        return ResponseEntity.ok(groupFromDB);
    }

    @PutMapping("/api/courses/{courseId}/groups/{groupId}/users")
    public ResponseEntity<?> addUsersToTheGroup(@PathVariable("groupId") Long groupId, @RequestBody List<Long> usersId) {
        Group updatedGroup = groupService.addUsers(groupId, usersId);
        if (updatedGroup != null) {
            return ResponseEntity.ok(modelMapper.map(updatedGroup, GroupMenuItemDTO.class));
        } else {
            return ResponseEntity.ok(new ApiError("Failed add users to the group"));
        }
    }

    @GetMapping("/api/courses/{courseId}/groups/{groupId}/users")
    public ResponseEntity<?> getUsersFromTheGroup(@PathVariable("courseId") Long courseId, @PathVariable("groupId") Long groupId) {
        Group updatedGroup = groupService.findGroupById(groupId);
        if (updatedGroup != null) {
            return ResponseEntity.ok(updatedGroup.getUsers());
        } else {
            return ResponseEntity.ok(new ApiError("Failed get users from the group"));
        }
    }
}
