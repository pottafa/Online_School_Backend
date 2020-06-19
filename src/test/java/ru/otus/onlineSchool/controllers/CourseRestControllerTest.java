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
class CourseRestControllerTest {
    @TestConfiguration
    static class CourseRestControllerTestContextConfiguration {
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
    void createCourseSuccess() throws Exception {
        Course course = new Course(10, "created test course");
        String courseJson = new ObjectMapper().writeValueAsString(course);

        mvc.perform(post("/api/courses")
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(content().json("10"))
                .andExpect(status().isOk());

        Course savedCourse = courseService.findCourseById(10);
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getTitle()).isEqualTo("created test course");
        assertThat(savedCourse.getId()).isEqualTo(10);
    }

    @Test
    void createCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Course course = new Course(1, "course with existed id");
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
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[?(@.id == 1 && @.title == 'Course One')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 2 && @.title == 'Course Two')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 3 && @.title == 'Course Three')]").exists());
    }

    @Test
    void deleteCourseSuccess() throws Exception {
        long courseId = 2;

        // Проверим, что такой курс есть
        Course course = courseService.findCourseById(courseId);
        assertThat(course).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/courses/" + courseId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        Course deletedCourse = courseService.findCourseById(courseId);
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
        long courseId = 3;

        // Проверим, что было в начале
        Course course = courseService.findCourseById(courseId);
        assertThat(course).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Course Three");
        assertThat(course.getDescription()).isNullOrEmpty();

        // Обновляем, сохраняем
        course.setTitle("Course Three Updated Title");
        course.setDescription("Course Three Updated Description");
        String courseJson = new ObjectMapper().writeValueAsString(course);
        mvc.perform(put("/api/courses/" + courseId)
                .contentType("application/json")
                .content(courseJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        Course updatedCourse = courseService.findCourseById(courseId);
        assertThat(updatedCourse).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Course Three Updated Title");
        assertThat(course.getDescription()).isEqualTo("Course Three Updated Description");
    }

    @Test
    void updateCourseErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update course");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long courseId = 3;

        // Проверим, что было в начале
        Course course = courseService.findCourseById(courseId);
        assertThat(course).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Course Three");
        assertThat(course.getDescription()).isNullOrEmpty();

        // Удалим курс
        courseService.deleteCourse(courseId);

        // Обновляем, сохраняем
        course.setTitle("Course Three Updated Title");
        course.setDescription("Course Three Updated Description");
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
        mvc.perform(get("/api/courses/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Course Two"));
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