package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.repository.CourseRepository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourseRestControllerTest {
    @TestConfiguration
    static class CourseRestControllerTestContextConfiguration {
        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CourseRepository courseRepository;


    @Test
    void createCourseSuccess() throws Exception {
        Course course = new Course();
        course.setTitle("Test course");

        String courseJson = new ObjectMapper().writeValueAsString(course);

        MvcResult result = mvc.perform(post("/api/courses")
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        long courseId = Long.parseLong(result.getResponse().getContentAsString());
        Course savedCourse = courseRepository.findById(courseId).orElse(null);
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getTitle()).isEqualTo("Test course");
        assertThat(savedCourse.getId()).isNotNull();
    }

    @Test
    void createCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Course course = new Course();
        course.setTitle("course with existed id");
        course.setId(103);

        String courseJson = new ObjectMapper().writeValueAsString(course);

        mvc.perform(post("/api/courses")
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllCoursesSuccess() throws Exception {
        mvc.perform(get("/api/courses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[?(@.id == 103 && @.title == 'Java SE')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 104 && @.title == 'Spring')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 105 && @.title == 'Spring Boot')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 106 && @.title == 'Java Script')]").exists());
    }

    @Test
    @Transactional
    void deleteCourseSuccess() throws Exception {
        long courseId = 103;

        // Проверим, что такой курс есть
        Course course = courseRepository.findById(courseId).orElse(null);
        assertThat(course).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/courses/" + courseId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        Course deletedCourse = courseRepository.findById(courseId).orElse(null);
        assertThat(deletedCourse).isNull();
    }

    @Test
    void deleteCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long notExistedCourseId = 10;

        mvc.perform(delete("/api/courses/" + notExistedCourseId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void updateCourseSuccess() throws Exception {
        long courseId = 104;

        // Проверим, что было в начале
        Course course = courseRepository.findById(courseId).orElse(null);
        assertThat(course).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Spring");
        assertThat(course.getDescription()).isEqualTo("Курс по Spring Framework");

        // Обновляем, сохраняем
        course.setDescription("Курс по Spring Framework Updated Description");

        String courseJson = new ObjectMapper().writeValueAsString(course);
        mvc.perform(put("/api/courses/" + courseId)
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        Course updatedCourse = courseRepository.findById(courseId).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(course.getDescription()).isEqualTo("Курс по Spring Framework Updated Description");
    }

    @Test
    void updateCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long courseId = 104;

        // Проверим, что было в начале
        Course course = courseRepository.findById(courseId).orElse(null);
        assertThat(course).isNotNull();

        // Удалим курс
        courseRepository.deleteById(courseId);

        // Обновляем, сохраняем
        course.setTitle("Updated Title");

        String courseJson = new ObjectMapper().writeValueAsString(course);

        mvc.perform(put("/api/courses/" + courseId)
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getCourseSuccess() throws Exception {
        mvc.perform(get("/api/courses/103"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(103))
                .andExpect(jsonPath("$.title").value("Java SE"));
    }

    @Test
    public void getCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long courseId = 10;

        mvc.perform(get("/api/courses/" + courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}