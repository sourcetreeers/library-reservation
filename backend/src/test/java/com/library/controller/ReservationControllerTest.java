package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.ReservationDTO;
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
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("创建预约 - 未登录返回401")
    void testCreateReservationNotLoggedIn() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setLibraryId(1L);
        dto.setSeatId(1L);
        // Use a far future time to avoid conflicts with past data
        dto.setStartTime(java.time.LocalDateTime.of(2026, 7, 15, 8, 0));
        dto.setEndTime(java.time.LocalDateTime.of(2026, 7, 15, 12, 0));

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(2)
    @DisplayName("创建预约 - 非学生用户返回403")
    void testCreateReservationByAdmin() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setLibraryId(1L);
        dto.setSeatId(1L);
        dto.setStartTime(java.time.LocalDateTime.of(2026, 7, 15, 8, 0));
        dto.setEndTime(java.time.LocalDateTime.of(2026, 7, 15, 12, 0));

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(getAdminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(3)
    @DisplayName("创建预约 - 学生成功预约")
    void testCreateReservationSuccess() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setLibraryId(1L);
        dto.setSeatId(1L);
        dto.setStartTime(java.time.LocalDateTime.of(2026, 7, 16, 8, 0));
        dto.setEndTime(java.time.LocalDateTime.of(2026, 7, 16, 12, 0));

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(getStudentSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("预约成功"))
                .andExpect(jsonPath("$.data.orderNo").exists());
    }

    @Test
    @Order(4)
    @DisplayName("查询我的预约列表 - 学生")
    void testGetMyReservations() throws Exception {
        mockMvc.perform(get("/reservation/page")
                        .param("current", "1")
                        .param("size", "10")
                        .session(getStudentSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @Order(5)
    @DisplayName("查询预约详情 - 根据订单号")
    void testGetReservationByOrderNo() throws Exception {
        mockMvc.perform(get("/reservation/order/20260316-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value("20260316-0001"));
    }

    @Test
    @Order(6)
    @DisplayName("查询预约详情 - 不存在的订单号")
    void testGetReservationByOrderNoNotFound() throws Exception {
        mockMvc.perform(get("/reservation/order/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("预约记录不存在"));
    }

    @Test
    @Order(7)
    @DisplayName("取消预约 - 未登录返回401")
    void testCancelReservationNotLoggedIn() throws Exception {
        mockMvc.perform(put("/reservation/2/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(8)
    @DisplayName("取消预约 - 学生取消自己的预约")
    void testCancelReservationSuccess() throws Exception {
        mockMvc.perform(put("/reservation/2/cancel").session(getStudentSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("取消成功"));
    }

    @Test
    @Order(9)
    @DisplayName("管理员查询所有预约")
    void testAdminPageQuery() throws Exception {
        mockMvc.perform(get("/reservation/page")
                        .param("current", "1")
                        .param("size", "10")
                        .session(getAdminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}
