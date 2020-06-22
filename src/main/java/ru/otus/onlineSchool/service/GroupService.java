package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    public Group findGroupById(Course course, long groupId) {
        return course.getGroups().stream()
                .filter(gr -> gr.getId() == groupId)
                .peek(gr -> LOGGER.info("Group with id {} was retrieved successfully", groupId))
                .findAny()
                .orElse(null);
    }

    @Transactional
    public Long createGroup(Long id, Group group) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            LOGGER.error("Failed create group. Course with id {} not exist", id);
            return null;
        }
        boolean isAdded = course.addGroup(group);
        if (!isAdded) {
            LOGGER.error("Error with adding group to course with id {}", id);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        List<Group> groups = updatedCourse.getGroups();
        Long groupId = groups.get(groups.size() - 1).getId();
        LOGGER.info("Group with id {} was successfully created", groupId);
        return groupId;
    }

    @Transactional
    public Course deleteGroup(Long courseId, Long groupId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed delete group. Course with id {} not exist", courseId);
            return null;
        }
        Group group = new Group();
        group.setId(groupId);
        boolean isDeleted = course.removeGroup(group);
        if (!isDeleted) {
            LOGGER.error("Error with deleting group in Course with id {}", courseId);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Group with id {} was successfully deleted", groupId);
        return updatedCourse;
    }

    @Transactional
    public Group updateGroup(Long courseId, Long groupId, Group group) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed update group. Course with id {} not exist", courseId);
            return null;
        }
        Group courseGroup = findGroupById(course, groupId);
        if (courseGroup == null) {
            LOGGER.error("Failed retrieve group from Course with id {}", courseId);
            return null;
        }
        courseGroup.setTitle(group.getTitle());
        boolean isUpdated = course.updateGroup(courseGroup);
        if (!isUpdated) {
            LOGGER.error("Failed update group in Course with id {}", courseId);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Group with id {} was successfully updated", groupId);
        return findGroupById(updatedCourse, groupId);
    }

    @Transactional
    public Group addUsers(Long courseId, Long groupId, List<Long> usersId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed add users. Course with id {} not exist", courseId);
            return null;
        }
        Group courseGroup = findGroupById(course, groupId);
        if (courseGroup == null) {
            LOGGER.error("Failed add users. Error while retrieving group from Course with id {}", courseId);
            return null;
        }
        List<User> users;
        try {
                   users = usersId.stream()
                    .map(userId -> userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException(String.valueOf(userId))))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            LOGGER.error("Failed add users. Error while getting users.", ex);
            return null;
        }
        courseGroup.setUsers(users);
        boolean isUpdated = course.updateGroup(courseGroup);
        if (!isUpdated) {
            LOGGER.error("Failed update group in Course with id {}", courseId);
            return null;
        }
        Course updatedCourse = courseRepository.save(course);
        LOGGER.info("Group with id {} was successfully updated", groupId);
        return courseGroup;
    }

}
