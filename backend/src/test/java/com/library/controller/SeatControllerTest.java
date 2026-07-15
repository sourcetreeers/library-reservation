package com.library.controller;

import com.library.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession getSession(String userType) {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setId(userType.equals("管理员") ? 1L : 2L);
        user.setUsername(userType.equals("管理员") ? "admin" : "student001");
        user.setUserType(userType);
        session.setAttribute("user", user);
        return session;
    }

    @Test
    @Order(1)
    @DisplayName("根据图书馆ID获取座位列表")
    void testGetSeatsByLibraryId() throws Exception {
        mockMvc.perform(get("/seat/library/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4));
    }

    @Test
    @Order(2)
    @DisplayName("获取可用座位")
    void testGetAvailableSeats() throws Exception {
        mockMvc.perform(get("/seat/available")
                        .param("libraryId", "1")
                        .param("startTime", "2026-06-29 08:00:00")
                        .param("endTime", "2026-06-29 12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("根据ID获取座位")
    void testGetSeatById() throws Exception {
        mockMvc.perform(get("/seat/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.seatNumber").value("001"));
    }

    @Test
    @Order(4)
    @DisplayName("根据ID获取座位 - 不存在")
    void testGetSeatByIdNotFound() throws Exception {
        mockMvc.perform(get("/seat/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("座位不存在"));
    }

    @Test
    @Order(5)
    @DisplayName("分页查询座位 - 无权限")
    void testPageQueryAsStudent() throws Exception {
        mockMvc.perform(get("/seat/page").session(getSession("学生")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(6)
    @DisplayName("分页查询座位 - 管理员")
    void testPageQueryAsAdmin() throws Exception {
        mockMvc.perform(get("/seat/page").session(getSession("图书馆管理员")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}
