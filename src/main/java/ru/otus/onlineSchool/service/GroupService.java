package ru.otus.onlineSchool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.onlineSchool.entity.*;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.GroupRepository;
import ru.otus.onlineSchool.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    @Autowired
    private GroupRepository groupRepository;

    public List<Group> findGroupByCourse(Long courseId) {
        List<Group> groups = new ArrayList<>();
        groupRepository.findByCourse_Id(courseId).forEach(groupDTO -> {
            Group group = new Group();
            group.setId(groupDTO.getId());
            group.setTitle(groupDTO.getTitle());
            groups.add(group);
        });
        return groups;
    }

    public Group findGroupById(long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    @Transactional
    public Long createGroup(Long courseId, Group groupFromDb) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            LOGGER.error("Failed create groupFromDb. Course with id {} not exist", courseId);
            return null;
        }
        groupFromDb.setCourse(course);
        Long groupFromDbId = groupRepository.save(groupFromDb).getId();
        LOGGER.info("Group with id {} was successfully created", groupFromDbId);
        return groupFromDbId;
    }


    @Transactional
    public boolean deleteGroup(Long groupFromDbId) {
        if (groupRepository.existsById(groupFromDbId)) {
            groupRepository.deleteById(groupFromDbId);
            LOGGER.info("Group with id {} was successfully deleted", groupFromDbId);
            return true;
        }
        LOGGER.error("Failed delete groupFromDb with id {}", groupFromDbId);
        return false;
    }


    @Transactional
    public Group updateGroup(Long groupId, Group group) {
        Group groupFromDB = groupRepository.findById(groupId).orElse(null);
        if (groupFromDB == null) {
            LOGGER.error("Failed update group. Group does not exist");
            return null;
        }
        groupFromDB.setTitle(group.getTitle());
        Group updatedGroup = groupRepository.save(groupFromDB);
        LOGGER.info("Group with id {} was successfully updated", updatedGroup.getId());
        return group;
    }


    @Transactional
    public Group addUsers(Long groupId, List<Long> usersId) {
        Group groupFromDb = findGroupById(groupId);
        if (groupFromDb == null) {
            LOGGER.error("Failed add users. Group with id {} doesn't exist", groupId);
            return null;
        }
        List<User> users;
        try {
            users = usersId.stream()
                    .map(userId -> userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(String.valueOf(userId))))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            LOGGER.error("Failed add users. Error while getting users.", ex);
            return null;
        }
        groupFromDb.setUsers(users);
        Group updatedGroup = groupRepository.save(groupFromDb);
        LOGGER.info("Group with id {} was successfully updated", updatedGroup.getId());
        return groupFromDb;
    }

}
