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
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.entity.UserProfile;
import ru.otus.onlineSchool.repository.FakeUserRepository;
import ru.otus.onlineSchool.repository.UserRepository;
import ru.otus.onlineSchool.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest()
@AutoConfigureMockMvc()
class UserRestControllerTest {
    @TestConfiguration
    static class CourseRestControllerTestContextConfiguration {
        @Bean
        @Primary
        @Scope()
        public UserRepository fakeWorkChartRepository() {
            return new FakeUserRepository();
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
    private UserService userService;

    @Autowired
    private FakeUserRepository fakeUserRepository;

    @BeforeEach
    void setUp() {
        fakeUserRepository.reset();
    }

    @Test
    void createUserSuccess() throws Exception {
        User user = new User(10, "test user", "test password");
        UserProfile userProfile = new UserProfile(10, "TestUser", "testemail@mail.com", 27);
        user.setProfile(userProfile);

        String userJson = new ObjectMapper().writeValueAsString(user);

        mvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(content().json("10"))
                .andExpect(status().isOk());

        User savedUser = userService.findUserById(10);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo("test user");
        assertThat(savedUser.getId()).isEqualTo(10);

        UserProfile savedProfile = savedUser.getProfile();
        assertThat(savedProfile).isNotNull();
        assertThat(savedProfile.getName()).isEqualTo("TestUser");
        assertThat(savedProfile.getEmail()).isEqualTo("testemail@mail.com");
        assertThat(savedProfile.getAge()).isEqualTo(27);
        assertThat(savedProfile.getId()).isEqualTo(10);
    }

    @Test
    void createUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create user");

        User userWithExistedId = new User(1, "test user", "test password");
        String userJson = new ObjectMapper().writeValueAsString(userWithExistedId);

        MvcResult result = mvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void getAllUsersSuccess() throws Exception {
        mvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[?(@.id == 1 && @.login == 'User one' && @.password == 'Password one' )]").exists())
                .andExpect(jsonPath("$.[?(@.id == 2 && @.login == 'User two' && @.password == 'Password two')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 3 && @.login == 'User three' && @.password == 'Password three')]").exists());
    }

    @Test
    void deleteUserSuccess() throws Exception {
        long userId = 2;

        // Проверим, что такой пользователь существует есть
        User user = userService.findUserById(userId);
        assertThat(user).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/users/" + userId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        User deletedUser = userService.findUserById(userId);
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete user");
        long notExistedUserId = 10;

        MvcResult result = mvc.perform(delete("/api/users/" + notExistedUserId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResult);

    }

    @Test
    void updateUserSuccess() throws Exception {
        long userId = 3;

        // Проверим, что было в начале
        User user = userService.findUserById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getLogin()).isEqualTo("User three");
        assertThat(user.getProfile()).isNull();

        // Обновляем, сохраняем
        user.setLogin("User three Updated login");
        user.setProfile(new UserProfile());
        String userJson = new ObjectMapper().writeValueAsString(user);
        mvc.perform(put("/api/users/" + userId)
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        User updatedUser = userService.findUserById(userId);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getLogin()).isEqualTo("User three Updated login");
        assertThat(updatedUser.getProfile()).isNotNull();
    }

    @Test
    void updateUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update user");
        long userId = 3;

        // Проверим, что было в начале
        User user = userService.findUserById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getLogin()).isEqualTo("User three");
        assertThat(user.getProfile()).isNull();

        // Удалим пользователя
        userService.deleteUser(userId);

        // Обновляем, сохраняем
        user.setLogin("User three Updated login");
        user.setProfile(new UserProfile());
        String userJson = new ObjectMapper().writeValueAsString(user);

        MvcResult result = mvc.perform(put("/api/users/" + userId)
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResult);
    }

    @Test
    public void getUserSuccess() throws Exception {
        mvc.perform(get("/api/users/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.login").value("User two"));
    }

    @Test
    public void getUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get user");
        long userId = 10;

        MvcResult result = mvc.perform(get("/api/users/" + userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedResult = new ObjectMapper().writeValueAsString(apiError);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResult);
    }


}