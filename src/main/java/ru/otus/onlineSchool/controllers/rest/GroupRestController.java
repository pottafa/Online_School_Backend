package ru.otus.onlineSchool.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.service.CourseService;
import ru.otus.onlineSchool.service.GroupService;

@RestController
public class GroupRestController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private GroupService groupService;

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
            return ResponseEntity.ok(course.getGroups());
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
        Course updatedCourse = groupService.updateGroup(courseId, groupId, group);
        if (updatedCourse == null) {
            return ResponseEntity.ok(new ApiError("Failed update group"));
        }
        return ResponseEntity.ok(null);
    }
}
