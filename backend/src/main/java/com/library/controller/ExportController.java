package com.library.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.Announcement;
import com.library.entity.Library;
import com.library.entity.Reservation;
import com.library.entity.Seat;
import com.library.entity.User;
import com.library.entity.ViolationRecord;
import com.library.aspect.Log;
import com.library.service.*;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 数据导出控制器
 */
@RestController
@RequestMapping({"/admin/export", "/system-admin/export"})
@CrossOrigin
public class ExportController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ViolationRecordService violationRecordService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private LibraryService libraryService;
    
    @Autowired
    private SeatService seatService;
    
    /**
     * 导出预约记录Excel
     */
    @Log(value = "导出", module = "预约记录")
    @GetMapping("/reservations")
    public void exportReservations(HttpSession session, HttpServletResponse response,
                                   @RequestParam(required = false) String status) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) return;
        
        setExportHeader(response, "预约记录.xlsx");
        
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        List<Reservation> list = reservationService.list(wrapper);
        
        // 转换为导出DTO列表
        List<ReservationExportData> exportList = convertReservationList(list);
        EasyExcel.write(response.getOutputStream(), ReservationExportData.class)
                .sheet("预约记录")
                .doWrite(exportList);
    }
    
    /**
     * 导出用户列表Excel
     */
    @Log(value = "导出", module = "用户列表")
    @GetMapping("/users")
    public void exportUsers(HttpSession session, HttpServletResponse response) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) return;
        
        setExportHeader(response, "用户列表.xlsx");
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        List<User> list = userService.list(wrapper);
        
        List<UserExportData> exportList = convertUserList(list);
        EasyExcel.write(response.getOutputStream(), UserExportData.class)
                .sheet("用户列表")
                .doWrite(exportList);
    }
    
    /**
     * 导出违规记录Excel
     */
    @Log(value = "导出", module = "违规记录")
    @GetMapping("/violations")
    public void exportViolations(HttpSession session, HttpServletResponse response,
                                  @RequestParam(required = false) String status) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) return;
        
        setExportHeader(response, "违规记录.xlsx");
        
        LambdaQueryWrapper<ViolationRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ViolationRecord::getStatus, status);
        }
        wrapper.orderByDesc(ViolationRecord::getViolationTime);
        List<ViolationRecord> list = violationRecordService.list(wrapper);
        
        List<ViolationExportData> exportList = convertViolationList(list);
        EasyExcel.write(response.getOutputStream(), ViolationExportData.class)
                .sheet("违规记录")
                .doWrite(exportList);
    }
    
    private void setExportHeader(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + encodedFileName);
    }
    
    // ==================== 导出数据类 ====================
    
    @lombok.Data
    public static class ReservationExportData {
        @com.alibaba.excel.annotation.ExcelProperty("流水号")
        private String orderNo;
        @com.alibaba.excel.annotation.ExcelProperty("用户姓名")
        private String userName;
        @com.alibaba.excel.annotation.ExcelProperty("图书室名称")
        private String libraryName;
        @com.alibaba.excel.annotation.ExcelProperty("座位号")
        private String seatNumber;
        @com.alibaba.excel.annotation.ExcelProperty("开始时间")
        private String startTime;
        @com.alibaba.excel.annotation.ExcelProperty("结束时间")
        private String endTime;
        @com.alibaba.excel.annotation.ExcelProperty("状态")
        private String status;
        @com.alibaba.excel.annotation.ExcelProperty("创建时间")
        private String createTime;
    }
    
    @lombok.Data
    public static class UserExportData {
        @com.alibaba.excel.annotation.ExcelProperty("ID")
        private Long id;
        @com.alibaba.excel.annotation.ExcelProperty("用户名")
        private String username;
        @com.alibaba.excel.annotation.ExcelProperty("姓名")
        private String realName;
        @com.alibaba.excel.annotation.ExcelProperty("手机号")
        private String phone;
        @com.alibaba.excel.annotation.ExcelProperty("类型")
        private String userType;
        @com.alibaba.excel.annotation.ExcelProperty("状态")
        private String status;
        @com.alibaba.excel.annotation.ExcelProperty("注册时间")
        private String createTime;
    }
    
    @lombok.Data
    public static class ViolationExportData {
        @com.alibaba.excel.annotation.ExcelProperty("ID")
        private Long id;
        @com.alibaba.excel.annotation.ExcelProperty("用户名")
        private String userName;
        @com.alibaba.excel.annotation.ExcelProperty("学号")
        private String studentNo;
        @com.alibaba.excel.annotation.ExcelProperty("违规类型")
        private String violationType;
        @com.alibaba.excel.annotation.ExcelProperty("扣分")
        private Integer pointsDeducted;
        @com.alibaba.excel.annotation.ExcelProperty("封禁天数")
        private Integer banDays;
        @com.alibaba.excel.annotation.ExcelProperty("状态")
        private String status;
        @com.alibaba.excel.annotation.ExcelProperty("违规时间")
        private String violationTime;
        @com.alibaba.excel.annotation.ExcelProperty("处理人")
        private String handledByName;
    }
    
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private List<ReservationExportData> convertReservationList(List<Reservation> list) {
        return list.stream().map(r -> {
            ReservationExportData d = new ReservationExportData();
            d.setOrderNo(r.getOrderNo());
            
            // 查询用户真实姓名
            User user = userService.getById(r.getUserId());
            d.setUserName(user != null ? (user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername()) : "未知用户");
            
            // 查询图书室名称
            Library library = libraryService.getById(r.getLibraryId());
            d.setLibraryName(library != null ? library.getName() : "未知图书室");
            
            // 查询座位号
            Seat seat = seatService.getById(r.getSeatId());
            d.setSeatNumber(seat != null ? seat.getSeatNumber() : "未知座位");
            
            d.setStartTime(r.getStartTime() != null ? r.getStartTime().format(dtf) : "");
            d.setEndTime(r.getEndTime() != null ? r.getEndTime().format(dtf) : "");
            d.setStatus(r.getStatus());
            d.setCreateTime(r.getCreateTime() != null ? r.getCreateTime().format(dtf) : "");
            return d;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    private List<UserExportData> convertUserList(List<User> list) {
        return list.stream().map(u -> {
            UserExportData d = new UserExportData();
            d.setId(u.getId());
            d.setUsername(u.getUsername());
            d.setRealName(u.getRealName());
            d.setPhone(u.getPhone());
            d.setUserType(u.getUserType());
            d.setStatus(u.getStatus());
            d.setCreateTime(u.getCreateTime() != null ? u.getCreateTime().format(dtf) : "");
            return d;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    private List<ViolationExportData> convertViolationList(List<ViolationRecord> list) {
        return list.stream().map(v -> {
            ViolationExportData d = new ViolationExportData();
            d.setId(v.getId());
            d.setUserName(v.getUserName());
            d.setStudentNo(v.getStudentNo());
            d.setViolationType(v.getViolationType());
            d.setPointsDeducted(v.getPointsDeducted());
            d.setBanDays(v.getBanDays());
            d.setStatus(v.getStatus());
            d.setViolationTime(v.getViolationTime() != null ? v.getViolationTime().format(dtf) : "");
            d.setHandledByName(v.getHandledByName());
            return d;
        }).collect(java.util.stream.Collectors.toList());
    }
}
