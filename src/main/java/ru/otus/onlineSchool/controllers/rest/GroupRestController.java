package ru.otus.onlineSchool.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.dto.CourseMenuItemDTO;
import ru.otus.onlineSchool.dto.GroupMenuItemDTO;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.service.CourseService;
import ru.otus.onlineSchool.service.GroupService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GroupRestController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/api/courses/{courseId}/groups/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable("courseId") long courseId, @PathVariable("groupId") long groupId) {
        Course course = courseService.findCourseById(courseId);
        if (course != null) {
            Group group = groupService.findGroupById(course, groupId);
            if (group != null) {
                return ResponseEntity.ok(group);
            }
        }
        return ResponseEntity.ok(new ApiError("Failed get group"));
    }

    @GetMapping("/api/courses/{id}/groups")
    public ResponseEntity<?> getGroups(@PathVariable("id") long id) {
        Course course = courseService.findCourseById(id);
        if (course != null) {
            List<GroupMenuItemDTO> groups = course.getGroups().stream()
                    .map(group -> modelMapper.map(group, GroupMenuItemDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(groups);
        } else {
            return ResponseEntity.ok(new ApiError("Failed get groups from course"));
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
    public ResponseEntity<?> deleteGroup(@PathVariable("courseId") Long courseId, @PathVariable("groupId") Long groupId) {
        Course updatedCourse = groupService.deleteGroup(courseId, groupId);
        if (updatedCourse != null) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(new ApiError("Failed delete group"));
        }
    }

    @PutMapping("/api/courses/{courseId}/groups/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("courseId") Long courseId, @PathVariable("groupId") Long groupId,
                                         @RequestBody Group group) {
        Group updatedGroup = groupService.updateGroup(courseId, groupId, group);
        if (updatedGroup == null) {
            return ResponseEntity.ok(new ApiError("Failed update group"));
        }
        return ResponseEntity.ok(modelMapper.map(updatedGroup, GroupMenuItemDTO.class));
    }


    @PutMapping("/api/courses/{courseId}/groups/{groupId}/users")
    public ResponseEntity<?> addUsersToTheGroup(@PathVariable("courseId") Long courseId, @PathVariable("groupId") Long groupId, @RequestBody List<Long> usersId) {
        Group updatedGroup = groupService.addUsers(courseId, groupId, usersId);
        if (updatedGroup != null) {
            return ResponseEntity.ok(modelMapper.map(updatedGroup, GroupMenuItemDTO.class));
        } else {
            return ResponseEntity.ok(new ApiError("Failed add users to the group"));
        }
    }

    @GetMapping("/api/courses/{courseId}/groups/{groupId}/users")
    public ResponseEntity<?> getUsersGromTheGroup(@PathVariable("courseId") Long courseId, @PathVariable("groupId") Long groupId) {
        Course course = courseService.findCourseById(courseId);
        Group updatedGroup = groupService.findGroupById(course, groupId);
        if (updatedGroup != null) {
            return ResponseEntity.ok(updatedGroup.getUsers());
        } else {
            return ResponseEntity.ok(new ApiError("Failed get users from the group"));
        }
    }
}
