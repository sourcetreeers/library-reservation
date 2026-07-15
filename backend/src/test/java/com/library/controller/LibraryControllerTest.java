package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession getStudentSession() {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setId(2L);
        user.setUsername("student001");
        user.setUserType("学生");
        session.setAttribute("user", user);
        return session;
    }

    private MockHttpSession getAdminSession() {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setUserType("图书馆管理员");
        session.setAttribute("user", user);
        return session;
    }

    @Test
    @Order(1)
    @DisplayName("获取图书馆列表 - 无需登录")
    void testGetLibraryList() throws Exception {
        mockMvc.perform(get("/library/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @Order(2)
    @DisplayName("根据ID获取图书馆")
    void testGetLibraryById() throws Exception {
        mockMvc.perform(get("/library/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("中央图书室"));
    }

    @Test
    @Order(3)
    @DisplayName("根据ID获取图书馆 - 不存在")
    void testGetLibraryByIdNotFound() throws Exception {
        mockMvc.perform(get("/library/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("图书馆不存在"));
    }

    @Test
    @Order(4)
    @DisplayName("分页查询图书馆 - 学生无权限")
    void testPageQueryAsStudent() throws Exception {
        mockMvc.perform(get("/library/page").session(getStudentSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(5)
    @DisplayName("分页查询图书馆 - 管理员有权限")
    void testPageQueryAsAdmin() throws Exception {
        mockMvc.perform(get("/library/page").session(getAdminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}
