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
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.onlineSchool.controllers.rest.message.ApiError;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Lesson;
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
class LessonRestControllerTest {
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
    void createLessonSuccess() throws Exception {
        Lesson lesson = new Lesson(10, "created test lesson", "test description");
        long courseId = 2L;
        String lessonJson = new ObjectMapper().writeValueAsString(lesson);

        mvc.perform(post("/api/courses/" + courseId + "/lessons")
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(content().json("10"))
                .andExpect(status().isOk());

        Course courseWithSavedLesson = courseService.findCourseById(courseId);
        Lesson savedLesson = findLessonById(courseWithSavedLesson, lesson.getId());
        assertThat(savedLesson).isNotNull();
        assertThat(savedLesson.getTitle()).isEqualTo("created test lesson");
        assertThat(savedLesson.getDescription()).isEqualTo("test description");
        assertThat(savedLesson.getId()).isEqualTo(10);
    }

    @Test
    void createLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create lesson");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        Lesson lesson = new Lesson(1, "created test lesson", "Test description");
        long courseId = 1L;
        String lessonJson = new ObjectMapper().writeValueAsString(lesson);

         mvc.perform(post("/api/courses/" + courseId + "/lessons")
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllLessonsSuccess() throws Exception {
        long courseId = 1;
        mvc.perform(get("/api/courses/" + courseId + "/lessons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[?(@.id == 1 && @.title == 'Lesson one')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 2 && @.title == 'Lesson two')]").exists());
    }

    @Test
    void deleteLessonSuccess() throws Exception {
        long courseId = 1;
        long lessonId = 2;

        Course course = courseService.findCourseById(courseId);
        assertThat(course).isNotNull();
        Lesson lesson = findLessonById(course, lessonId);
        assertThat(lesson).isNotNull();

        mvc.perform(delete("/api/courses/" + courseId + "/lessons/" + lessonId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        Course updatedCourse = courseService.findCourseById(courseId);
        Lesson deletedLesson = findLessonById(updatedCourse, lessonId);
        assertThat(deletedLesson).isNull();
    }

    @Test
    void deleteLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete lesson");
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 10;
        long notExistedLessonId = 10;

        mvc.perform(delete("/api/courses/" + courseId + "/lessons/" + notExistedLessonId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    void updateLessonSuccess() throws Exception {
        long courseId = 1;
        long lessonId = 1;

        Course course = courseService.findCourseById(courseId);
        Lesson lesson = findLessonById(course, lessonId);
        assertThat(lesson).isNotNull();
        assertThat(lesson.getTitle()).isEqualTo("Lesson one");
        assertThat(lesson.getDescription()).isEqualTo("Some description");

        lesson.setTitle("Lesson one Updated Title");
        lesson.setDescription("Lesson one Updated Description");

        String lessonJson = new ObjectMapper().writeValueAsString(lesson);
        mvc.perform(put("/api/courses/" + courseId + "/lessons/" + lessonId)
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk());

        Course updatedCourse = courseService.findCourseById(courseId);
        Lesson updatedLesson = findLessonById(updatedCourse, lessonId);
        assertThat(updatedLesson).isNotNull();
        assertThat(updatedLesson.getTitle()).isEqualTo("Lesson one Updated Title");
        assertThat(updatedLesson.getDescription()).isEqualTo("Lesson one Updated Description");
    }

    @Test
    void updateLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update lesson");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 1;
        long lessonId = 1;

        Course course = courseService.findCourseById(courseId);
        Lesson lesson = findLessonById(course, lessonId);
        assertThat(lesson).isNotNull();
        assertThat(lesson.getTitle()).isEqualTo("Lesson one");

        course.removeLesson(lesson);

        lesson.setTitle("Lesson one Updated Title");
        lesson.setDescription("Lesson one Updated Description");
        String lessonJson = new ObjectMapper().writeValueAsString(lesson);
        mvc.perform(put("/api/courses/" + courseId + "/lessons/" + lessonId)
                .contentType("application/json")
                .content(lessonJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getLessonSuccess() throws Exception {
        long courseId = 1;
        long lessonId = 1;
        mvc.perform(get("/api/courses/" + courseId + "/lessons/" + lessonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson one"))
                .andExpect(jsonPath("$.description").value("Some description"));
    }

    @Test
    public void getLessonErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get lesson");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        long courseId = 1;
        long lessonId = 10;
        mvc.perform(get("/api/courses/" + courseId + "/lessons/" + lessonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    private Lesson findLessonById(Course course, long lessonId) {
      return course.getLessons().stream()
                .filter(les -> les.getId() == lessonId)
                .findFirst()
                .orElse(null);
    }
}