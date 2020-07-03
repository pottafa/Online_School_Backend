package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.GroupRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GroupRestControllerTest {
    @TestConfiguration
    static class GroupRestControllerTestContextConfiguration {
        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    void createGroupSuccess() throws Exception {
        long courseId = 103;
        Course courseFromDb = courseRepository.findById(courseId).orElse(null);

        Group group = new Group();
        group.setTitle("created test group");
        group.setCourse(courseFromDb);

        String groupJson = new ObjectMapper().writeValueAsString(group);

        MvcResult result = mvc.perform(post("/api/courses/" + courseId + "/groups")
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Long groupId = Long.parseLong(result.getResponse().getContentAsString());
        Group savedGroup = groupRepository.findById(groupId).orElse(null);
        assertThat(savedGroup).isNotNull();
        assertThat(savedGroup.getId()).isNotNull();
        assertThat(savedGroup.getTitle()).isEqualTo("created test group");

    }

    @Test
    void createGroupErrorMessage() throws Exception {
        int notExistCourseId = 55;

        ApiError apiError = new ApiError("Failed create group");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Group group = new Group();
        group.setTitle("Test title");
        group.setCourse(null);

        String groupJson = new ObjectMapper().writeValueAsString(group);

        mvc.perform(post("/api/courses/" + notExistCourseId + "/groups")
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllGroupsSuccess() throws Exception {
        long courseId = 103;
        mvc.perform(get("/api/courses/" + courseId + "/groups"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[?(@.id == 114 && @.title == 'First group')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 118 && @.title == 'Second group')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 119 && @.title == 'Third group')]").exists());
    }

    @Test
    @Transactional
    void deleteGroupSuccess() throws Exception {
        long courseId = 103;
        long groupId = 114;

        // Проверим, что такая группа есть
        Group group = groupRepository.findById(groupId).orElse(null);
        assertThat(group).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/courses/" + courseId + "/groups/" + groupId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        Group deletedGroup = groupRepository.findById(groupId).orElse(null);
        assertThat(deletedGroup).isNull();
    }

    @Test
    void deleteGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long notExistedGroupId = 200;

        mvc.perform(delete("/api/courses/" + courseId + "/groups/" + notExistedGroupId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    void updateGroupSuccess() throws Exception {
        long courseId = 103;
        long groupId = 114;


        Group group = groupRepository.findById(groupId).orElse(null);
        assertThat(group).isNotNull();
        assertThat(group.getTitle()).isEqualTo("First group");

        // Обновляем, сохраняем
        group.setTitle("First group Updated Title");
        group.setUsers(null);

        String groupJson = new ObjectMapper().writeValueAsString(group);
        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId)
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        Group updatedGroup = groupRepository.findById(groupId).orElse(null);
        assertThat(updatedGroup).isNotNull();
        assertThat(updatedGroup.getTitle()).isEqualTo("First group Updated Title");
    }


    @Test
    void updateGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long groupId = 1111;

        Group group = new Group();
        group.setTitle("First group Updated Title");
        group.setUsers(null);

        String groupJson = new ObjectMapper().writeValueAsString(group);

        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId)
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void getGroupSuccess() throws Exception {
        long courseId = 103;
        long groupId = 114;
        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(114))
                .andExpect(jsonPath("$.title").value("First group"));
    }

    @Test
    public void getGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long groupId = 200;
        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    @Transactional
    void addUsersToTheGroupSuccess() throws Exception {

        int courseId = 103;
        long groupId = 114;
        List<Integer> usersIds = Arrays.asList(115, 116);

        String usersIdsJson = new ObjectMapper().writeValueAsString(usersIds);

        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId + "/users")
                .contentType("application/json")
                .content(usersIdsJson))
                .andDo(print())
                .andExpect(status().isOk());

        Group group = groupRepository.findById(groupId).orElse(null);
        assertThat(group).isNotNull();
        List<User> users = group.getUsers();
        assertThat(users).isNotNull();
        User user1 = users.get(0);
        assertThat(user1.getId()).isEqualTo(115);
        User user2 = users.get(1);
        assertThat(user2.getId()).isEqualTo(116);

    }


    @Test
    void addUsersToTheGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed add users to the group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        int courseId = 103;
        int groupId = 1111;
        List<Integer> usersIds = Arrays.asList(115, 116);

        String usersIdsJson = new ObjectMapper().writeValueAsString(usersIds);

        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId + "/users")
                .contentType("application/json")
                .content(usersIdsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }


    @Test
    void getUsersToTheGroupSuccess() throws Exception {
        int courseId = 103;
        int groupId = 114;

        List<Integer> usersIds = Arrays.asList(115, 116);

        String usersIdsJson = new ObjectMapper().writeValueAsString(usersIds);

        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId + "/users")
                .contentType("application/json")
                .content(usersIdsJson))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId + "/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[?(@.id == 115 && @.login == 'teacher')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 116 && @.login == 'student')]").exists());

    }

    @Test
    void getUsersFromTheGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get users from the group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        int courseId = 103;
        int groupId = 111111;

        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId + "/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedResult));

    }

}