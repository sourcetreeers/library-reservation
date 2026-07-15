package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession studentSession;
    private static MockHttpSession adminSession;

    @Test
    @Order(1)
    @DisplayName("登录成功 - 学生用户")
    void testLoginSuccess() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("student001");
        dto.setPassword("123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.username").value("student001"))
                .andExpect(jsonPath("$.data.userType").value("学生"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andReturn();

        studentSession = new MockHttpSession();
        String responseBody = result.getResponse().getContentAsString();
        studentSession.setAttribute("user",
                objectMapper.readValue(
                        objectMapper.readTree(responseBody).get("data").toString(),
                        User.class));
    }

    @Test
    @Order(2)
    @DisplayName("登录失败 - 错误密码")
    void testLoginFailWrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("student001");
        dto.setPassword("wrong_password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(3)
    @DisplayName("登录失败 - 不存在的用户")
    void testLoginFailUserNotFound() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("nonexistent");
        dto.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(4)
    @DisplayName("注册成功")
    void testRegisterSuccess() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newstudent");
        dto.setPassword("123456");
        dto.setRealName("新同学");
        dto.setPhone("13800138001");
        dto.setEmail("new@test.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("newstudent"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @Order(5)
    @DisplayName("注册失败 - 用户名已存在")
    void testRegisterFailDuplicateUsername() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("student001");
        dto.setPassword("123456");
        dto.setRealName("重复用户");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(6)
    @DisplayName("获取当前用户 - 已登录")
    void testGetCurrentUserLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setId(2L);
        user.setUsername("student001");
        user.setUserType("学生");
        session.setAttribute("user", user);

        mockMvc.perform(get("/auth/current").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("student001"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @Order(7)
    @DisplayName("获取当前用户 - 未登录返回401")
    void testGetCurrentUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/auth/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未登录"));
    }

    @Test
    @Order(8)
    @DisplayName("登录成功 - 管理员用户")
    void testAdminLoginSuccess() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userType").value("图书馆管理员"))
                .andReturn();

        adminSession = new MockHttpSession();
        String responseBody = result.getResponse().getContentAsString();
        adminSession.setAttribute("user",
                objectMapper.readValue(
                        objectMapper.readTree(responseBody).get("data").toString(),
                        User.class));
    }

    @Test
    @Order(9)
    @DisplayName("登出成功")
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setId(2L);
        user.setUsername("student001");
        session.setAttribute("user", user);

        mockMvc.perform(post("/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }
}
