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
import ru.otus.onlineSchool.entity.User;
import ru.otus.onlineSchool.repository.UserRepository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {
    @TestConfiguration
    static class UserRestControllerTestContextConfiguration {
        @Bean
        @Primary
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void createUserSuccess() throws Exception {
        User user = new User();
        user.setLogin("Test login");
        user.setPassword("test");

        String userJson = new ObjectMapper().writeValueAsString(user);

        MvcResult result = mvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        long userId = Long.parseLong(result.getResponse().getContentAsString());
        User savedUser = userRepository.findById(userId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo("Test login");
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void createUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed create user");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);

        User user = new User();
        user.setLogin("user with existed id");
        user.setId(116);

        String userJson = new ObjectMapper().writeValueAsString(user);

        mvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getAllUsersSuccess() throws Exception {
        mvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[?(@.id == 115 && @.login == 'teacher')]").exists())
                .andExpect(jsonPath("$.[?(@.id == 116)]").exists())
                .andExpect(jsonPath("$.[?(@.id == 117 && @.login == 'admin')]").exists());
    }

    @Test
    @Transactional
    void deleteUserSuccess() throws Exception {
        long userId = 116;

        // Проверим, что такой пользователь есть
        User user = userRepository.findById(userId).orElse(null);
        assertThat(user).isNotNull();

        // Удаляем
        mvc.perform(delete("/api/users/" + userId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что удалили
        User deletedUser = userRepository.findById(userId).orElse(null);
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed delete user");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long notExistedUserId = 10;

        mvc.perform(delete("/api/users/" + notExistedUserId)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void updateUserSuccess() throws Exception {
        long userId = 116;

        // Проверим, что было в начале
        User user = userRepository.findById(userId).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getLogin()).isEqualTo("student");
        // Обновляем, сохраняем
        user.setLogin("student Updated Login");
        user.setRoles(null);

        String userJson = new ObjectMapper().writeValueAsString(user);
        mvc.perform(put("/api/users/" + userId)
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверяем, что сохранилось
        User updatedUser = userRepository.findById(userId).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(user.getLogin()).isEqualTo("student Updated Login");
    }

    @Test
    void updateUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed update user");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long userId = 117;

        User user = new User();
        user.setLogin("Updated Login");
        user.setRoles(null);

        String userJson = new ObjectMapper().writeValueAsString(user);

        mvc.perform(put("/api/users/" + userId)
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void getUserSuccess() throws Exception {
        mvc.perform(get("/api/users/117"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(117))
                .andExpect(jsonPath("$.login").value("admin"));
    }

    @Test
    public void getUserErrorMessage() throws Exception {
        ApiError apiError = new ApiError("Failed get user");
        String expectedResponse = new ObjectMapper().writeValueAsString(apiError);
        long userId = 10;

        mvc.perform(get("/api/users/" + userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}