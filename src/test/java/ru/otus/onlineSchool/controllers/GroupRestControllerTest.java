package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.FakeCourseRepository;
import ru.otus.onlineSchool.service.CourseService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest()
@AutoConfigureMockMvc()
class GroupRestControllerTest {
    @TestConfiguration
    static class GroupRestControllerTestContextConfiguration {
        @Bean
        @Primary
        @Scope()
        public CourseRepository fakeWorkChartRepository() {
            return new FakeCourseRepository();
        }

        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CourseService courseService;
    @Autowired
    private FakeCourseRepository fakeCourseRepository;

    @BeforeEach
    void setUp() {
        fakeCourseRepository.reset();
    }

    @Test
    void createGroupSuccess() throws Exception {
        Group group = new Group(10, "created test group");
        long courseId = 2L;
        String groupJson = new ObjectMapper().writeValueAsString(group);

        mvc.perform(post("/api/courses/" + courseId + "/groups")
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(content().json("10"))
                .andExpect(status().isOk());

        Course courseWithSavedGroup = courseService.findCourseById(courseId);
        Group savedGroup = findGroupById(courseWithSavedGroup, group.getId());
        assertThat(savedGroup).isNotNull();
        assertThat(savedGroup.getTitle()).isEqualTo("created test group");
        assertThat(savedGroup.getId()).isEqualTo(10);
    }

    @Test
    void createGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create group");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Group group = new Group(1, "created test group");
        long courseId = 1L;
        String groupJson = new ObjectMapper().writeValueAsString(group);

        mvc.perform(post("/api/courses/" + courseId + "/groups")
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllGroupsSuccess() throws Exception {
        long courseId = 1;
        mvc.perform(get("/api/courses/" + courseId + "/groups"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[?(@.id == 1 && @.title == 'Group one')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 2 && @.title == 'Group two')]").exists());
    }

    @Test
    void deleteGroupSuccess() throws Exception {
        long courseId = 1;
        long groupId = 2;

        // Проверим, что такая группа есть
        Course course = courseService.findCourseById(courseId);
        assertThat(course).isNotNull();
        Group group = findGroupById(course, groupId);
        assertThat(group).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/courses/" + courseId + "/groups/" + groupId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        Course updatedCourse = courseService.findCourseById(courseId);
        Group deletedGroup = findGroupById(updatedCourse, groupId);
        assertThat(deletedGroup).isNull();
    }

    @Test
    void deleteGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 10;
        long notExistedGroupId = 10;

        mvc.perform(delete("/api/courses/" + courseId + "/groups/" + notExistedGroupId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    void updateGroupSuccess() throws Exception {
        long courseId = 1;
        long groupId = 1;

        Course course = courseService.findCourseById(courseId);
        Group group = findGroupById(course, groupId);
        assertThat(group).isNotNull();
        assertThat(group.getTitle()).isEqualTo("Group one");

        // Обновляем, сохраняем
        group.setTitle("Group one Updated Title");

        String groupJson = new ObjectMapper().writeValueAsString(group);
        mvc.perform(put("/api/courses/" + courseId + "/groups/" + groupId)
                .contentType("application/json")
                .content(groupJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        Course updatedCourse = courseService.findCourseById(courseId);
        Group updatedGroup = findGroupById(updatedCourse, groupId);
        assertThat(updatedGroup).isNotNull();
        assertThat(updatedGroup.getTitle()).isEqualTo("Group one Updated Title");
    }

    @Test
    void updateGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 1;
        long groupId = 1;

        Course course = courseService.findCourseById(courseId);
        Group group = findGroupById(course, groupId);
        assertThat(group).isNotNull();
        assertThat(group.getTitle()).isEqualTo("Group one");

        // Удалим группу
        course.removeGroup(group);

        // Обновляем, сохраняем
        group.setTitle("Group one Updated Title");
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
        long courseId = 1;
        long groupId = 1;
        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Group one"));
    }

    @Test
    public void getGroupErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get group");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 1;
        long groupId = 10;
        mvc.perform(get("/api/courses/" + courseId + "/groups/" + groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }


    private Group findGroupById(Course course, long groupId) {
        return course.getGroups().stream()
                .filter(gr -> gr.getId() == groupId)
                .findFirst()
                .orElse(null);
    }
}