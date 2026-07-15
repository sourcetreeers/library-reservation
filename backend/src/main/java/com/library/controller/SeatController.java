package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.aspect.Log;
import com.library.entity.Seat;
import com.library.entity.User;
import com.library.service.SeatService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import com.library.vo.SeatStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 座位控制器
 */
@RestController
@RequestMapping("/seat")
@CrossOrigin
public class SeatController {
    
    @Autowired
    private SeatService seatService;
    
    /**
     * 根据图书馆ID查询座位列表
     */
    @GetMapping("/library/{libraryId}")
    public Result<List<Seat>> getByLibraryId(@PathVariable Long libraryId) {
        List<Seat> seats = seatService.getByLibraryId(libraryId);
        return Result.success(seats);
    }
    
    /**
     * 查询可用座位
     */
    @GetMapping("/available")
    public Result<List<Seat>> getAvailableSeats(@RequestParam Long libraryId,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Seat> seats = seatService.getAvailableSeats(libraryId, startTime, endTime);
        return Result.success(seats);
    }
    
    /**
     * 获取图书馆所有座位及其预约状态
     */
    @GetMapping("/all-with-status")
    public Result<List<SeatStatusVO>> getAllSeatsWithStatus(@RequestParam Long libraryId,
                                                     @RequestParam String startTime,
                                                     @RequestParam String endTime) {
        try {
            // 手动解析时间字符串，支持多种格式
            LocalDateTime start = parseDateTime(startTime);
            LocalDateTime end = parseDateTime(endTime);
            
            List<SeatStatusVO> seats = seatService.getAllSeatsWithStatus(libraryId, start, end);
            return Result.success(seats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("时间格式错误: " + e.getMessage());
        }
    }
    
    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        // 替换 + 号为空格（URL编码可能导致的问题）
        dateTimeStr = dateTimeStr.replace("+", " ");
        
        // 尝试多种格式
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-ddTHH:mm:ss",
            "yyyy-MM-dd HH:mm"
        };
        
        for (String pattern : patterns) {
            try {
                return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern(pattern));
            } catch (Exception e) {
                // 尝试下一个格式
            }
        }
        
        throw new IllegalArgumentException("无法解析时间格式: " + dateTimeStr);
    }
    
    /**
     * 获取实时座位监控布局（管理员用，基于当前时间）
     */
    @GetMapping("/monitor/layout")
    public Result<List<SeatStatusVO>> getMonitorLayout(@RequestParam Long libraryId) {
        try {
            List<SeatStatusVO> seats = seatService.getSeatMonitorLayout(libraryId);
            return Result.success(seats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取监控布局失败: " + e.getMessage());
        }
    }

    /**
     * 获取座位布局（含行列号，学生选座用）
     */
    @GetMapping("/layout")
    public Result<List<SeatStatusVO>> getSeatsLayout(@RequestParam Long libraryId,
                                                     @RequestParam String startTime,
                                                     @RequestParam String endTime) {
        try {
            LocalDateTime start = parseDateTime(startTime);
            LocalDateTime end = parseDateTime(endTime);
            List<SeatStatusVO> seats = seatService.getAllSeatsWithStatusAndLayout(libraryId, start, end);
            return Result.success(seats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("时间格式错误: " + e.getMessage());
        }
    }

    /**
     * 分页查询座位（管理员端使用）
     */
    @GetMapping("/page")
    public Result<PageResult<Seat>> page(@RequestParam(defaultValue = "1") int current,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Long libraryId,
                                        @RequestParam(required = false) String seatNumber,
                                        @RequestParam(required = false) String seatType,
                                        @RequestParam(required = false) String status,
                                        HttpSession session) {
        // 检查管理员权限（图书馆管理员或系统管理员）
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限访问");
        }
        
        // 使用手写SQL分页查询
        PageResult<Seat> result = seatService.pageQuery(current, size, libraryId, seatNumber, seatType, status);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询座位
     */
    @GetMapping("/{id}")
    public Result<Seat> getById(@PathVariable Long id) {
        Seat seat = seatService.getById(id);
        if (seat == null) {
            return Result.error("座位不存在");
        }
        return Result.success(seat);
    }
    
    /**
     * 新增座位
     */
    @Log(value = "新增", module = "座位")
    @PostMapping
    public Result<Object> save(@RequestBody Seat seat, HttpSession session) {
        // 检查管理员权限（图书馆管理员或系统管理员）
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }
        
        // 检查座位编号是否重复
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getLibraryId, seat.getLibraryId())
               .eq(Seat::getSeatNumber, seat.getSeatNumber());
        if (seatService.count(wrapper) > 0) {
            return Result.error("该图书馆已存在相同编号的座位");
        }
        
        seat.setStatus("正常");
        seatService.save(seat);
        return Result.success("新增成功");
    }
    
    /**
     * 更新座位
     */
    @Log(value = "修改", module = "座位")
    @PutMapping("/{id}")
    public Result<Object> update(@PathVariable Long id, @RequestBody Seat seat, HttpSession session) {
        // 检查管理员权限（图书馆管理员或系统管理员）
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }
        
        // 检查座位编号是否重复（排除自己）
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getLibraryId, seat.getLibraryId())
               .eq(Seat::getSeatNumber, seat.getSeatNumber())
               .ne(Seat::getId, id);
        if (seatService.count(wrapper) > 0) {
            return Result.error("该图书馆已存在相同编号的座位");
        }
        
        seat.setId(id);
        seatService.updateById(seat);
        return Result.success("更新成功");
    }
    
    /**
     * 删除座位
     */
    @Log(value = "删除", module = "座位")
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable Long id, HttpSession session) {
        // 检查管理员权限（图书馆管理员或系统管理员）
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }
        
        seatService.removeById(id);
        return Result.success("删除成功");
    }
}