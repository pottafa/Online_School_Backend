package ru.otus.onlineSchool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
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
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.repository.CourseRepository;
import ru.otus.onlineSchool.repository.LessonRepository;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LessonRestControllerTest {
    @TestConfiguration
    static class LessonRestControllerTestContextConfiguration {
        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    void createLessonSuccess() throws Exception {
        Long courseId = 103L;
        Course courseFromDb = courseRepository.findById(courseId).orElse(null);

        Lesson lesson = new Lesson();
        lesson.setTitle("created test lesson");
        lesson.setCourse(courseFromDb);

        String lessonJson = new ObjectMapper().writeValueAsString(lesson);

        MvcResult result = mvc.perform(post("/api/courses/" + courseId + "/lessons")
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Long lessonId = Long.parseLong(result.getResponse().getContentAsString());
        Lesson savedLesson = lessonRepository.findById(lessonId).orElse(null);
        assertThat(savedLesson).isNotNull();
        assertThat(savedLesson.getId()).isNotNull();
        assertThat(savedLesson.getTitle()).isEqualTo("created test lesson");

    }

    @Test
    void createLessonErrorMessage() throws Exception {
        long notExistCourseId = 55;

        ApiError apiError = new ApiError("Failed create lesson");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test title");
        lesson.setCourse(null);

        String lessonJson = new ObjectMapper().writeValueAsString(lesson);

        mvc.perform(post("/api/courses/" + notExistCourseId + "/lessons")
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllLessonsSuccess() throws Exception {
        long courseId = 103;
        mvc.perform(get("/api/courses/" + courseId + "/lessons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[?(@.id == 107 && @.title == 'Введение')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 108 && @.title == 'Коллекции')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 109 && @.title == 'Рефлекшн')]").exists());
    }

    @Test
    @Transactional
    void deleteLessonSuccess() throws Exception {
        long courseId = 103;
        long lessonId = 107;

        // Проверим, что такая группа есть
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        assertThat(lesson).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/courses/" + courseId + "/lessons/" + lessonId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        Lesson deletedLesson = lessonRepository.findById(lessonId).orElse(null);
        assertThat(deletedLesson).isNull();
    }

    @Test
    void deleteLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete lesson");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long notExistedLessonId = 200;

        mvc.perform(delete("/api/courses/" + courseId + "/lessons/" + notExistedLessonId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    void updateLessonSuccess() throws Exception {
        long courseId = 103;
        long lessonId = 107;

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        assertThat(lesson).isNotNull();
        assertThat(lesson.getTitle()).isEqualTo("Введение");

        // Обновляем, сохраняем
        lesson.setTitle("Введение Updated Title");

        String lessonJson = new ObjectMapper().writeValueAsString(lesson);
        mvc.perform(put("/api/courses/" + courseId + "/lessons/" + lessonId)
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        Lesson updatedLesson = lessonRepository.findById(lessonId).orElse(null);
        assertThat(updatedLesson).isNotNull();
        assertThat(updatedLesson.getTitle()).isEqualTo("Введение Updated Title");
    }


    @Test
    void updateLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update lesson");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long lessonId = 1111;

        Lesson lesson = new Lesson();
        lesson.setTitle("Updated Title");

        String lessonJson = new ObjectMapper().writeValueAsString(lesson);

        mvc.perform(put("/api/courses/" + courseId + "/lessons/" + lessonId )
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void getLessonSuccess() throws Exception {
        long courseId = 103;
        long lessonId = 107;
        mvc.perform(get("/api/courses/" + courseId + "/lessons/" + lessonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(107))
                .andExpect(jsonPath("$.title").value("Введение"));
    }

    @Test
    public void getLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get lesson");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 103;
        long lessonId = 200;
        mvc.perform(get("/api/courses/" + courseId + "/lessons/" + lessonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

}