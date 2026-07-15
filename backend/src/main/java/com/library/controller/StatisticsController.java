package com.library.controller;

import com.library.entity.Reservation;
import com.library.entity.Seat;
import com.library.entity.User;
import com.library.mapper.LibraryMapper;
import com.library.mapper.ReservationMapper;
import com.library.mapper.SeatMapper;
import com.library.mapper.UserMapper;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/admin/statistics")
@CrossOrigin
public class StatisticsController {
    
    @Autowired
    private LibraryMapper libraryMapper;
    
    @Autowired
    private SeatMapper seatMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取各图书室使用情况统计
     */
    @GetMapping("/library-usage")
    public Result<List<Map<String, Object>>> getLibraryUsage(@RequestParam(required = false) Long filterLibraryId) {
        try {
            // 获取所有图书室（如果指定了图书室则只查单个）
            List<com.library.entity.Library> libraries;
            if (filterLibraryId != null) {
                com.library.entity.Library lib = libraryMapper.selectById(filterLibraryId);
                libraries = lib != null ? Collections.singletonList(lib) : Collections.emptyList();
            } else {
                libraries = libraryMapper.selectList(null);
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (com.library.entity.Library library : libraries) {
                Long libraryId = library.getId();
                
                // 统计该图书室的座位总数
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Seat> seatWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                seatWrapper.eq(Seat::getLibraryId, libraryId);
                List<Seat> seats = seatMapper.selectList(seatWrapper);
                int totalSeats = seats.size();
                
                // 统计今日预约数
                LocalDate today = LocalDate.now();
                LocalDateTime dayStart = today.atStartOfDay();
                LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
                
                List<Reservation> allReservations = reservationMapper.selectList(null);
                
                long todayReservations = allReservations.stream()
                    .filter(r -> {
                        if (r == null || r.getStartTime() == null || r.getLibraryId() == null) {
                            return false;
                        }
                        LocalDateTime startTime = r.getStartTime();
                        return !startTime.isBefore(dayStart) && 
                               startTime.isBefore(dayEnd) &&
                               libraryId.equals(r.getLibraryId());
                    })
                    .count();
                
                // 计算使用率（今日已使用座位数 / 总座位数）
                double usageRate = totalSeats > 0 ? (double) todayReservations / totalSeats * 100 : 0;
                
                Map<String, Object> stats = new HashMap<>();
                stats.put("libraryId", libraryId);
                stats.put("libraryName", library.getName());
                stats.put("totalSeats", totalSeats);
                stats.put("todayReservations", todayReservations);
                stats.put("usageRate", String.format("%.1f", usageRate));
                
                result.add(stats);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("统计数据查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取座位类型分布统计
     */
    @GetMapping("/seat-type-distribution")
    public Result<Map<String, Object>> getSeatTypeDistribution(@RequestParam(required = false) Long libraryId) {
        List<Seat> seats;
        
        if (libraryId != null) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Seat> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(Seat::getLibraryId, libraryId);
            seats = seatMapper.selectList(wrapper);
        } else {
            seats = seatMapper.selectList(null);
        }
        
        // 按座位类型分组统计
        Map<String, Long> typeCount = seats.stream()
            .collect(Collectors.groupingBy(
                Seat::getSeatType,
                Collectors.counting()
            ));
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", seats.size());
        result.put("distribution", typeCount);
        
        return Result.success(result);
    }
    
    /**
     * 获取预约状态统计
     */
    @GetMapping("/reservation-status")
    public Result<Map<String, Long>> getReservationStatus(@RequestParam(required = false) Long libraryId) {
        try {
            List<Reservation> reservations = reservationMapper.selectList(null);
            
            // 如果指定了图书馆，过滤
            if (libraryId != null) {
                final Long finalLibraryId = libraryId;
                reservations = reservations.stream()
                    .filter(r -> r != null && finalLibraryId.equals(r.getLibraryId()))
                    .collect(Collectors.toList());
            }
            
            // 按状态分组统计
            Map<String, Long> statusCount = reservations.stream()
                .filter(r -> r != null && r.getStatus() != null)
                .collect(Collectors.groupingBy(
                    Reservation::getStatus,
                    Collectors.counting()
                ));
            
            return Result.success(statusCount);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预约状态统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取高峰时段分布（24小时各时段预约数）
     */
    @GetMapping("/peak-hours")
    public Result<List<Map<String, Object>>> getPeakHours(@RequestParam(required = false) Long libraryId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            List<Reservation> reservations = reservationMapper.selectList(null);

            // 初始化24小时数据
            int[] hourCounts = new int[24];
            for (Reservation r : reservations) {
                if (r == null || r.getStartTime() == null) continue;
                if (libraryId != null && !libraryId.equals(r.getLibraryId())) continue;
                int hour = r.getStartTime().getHour();
                hourCounts[hour]++;
            }

            String[] labels = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00",
                    "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
                    "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
                    "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

            for (int i = 0; i < 24; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("hour", labels[i]);
                item.put("count", hourCounts[i]);
                result.add(item);
            }

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("高峰时段查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户履约排行榜
     */
    @GetMapping("/user-behavior")
    public Result<List<Map<String, Object>>> getUserBehavior(
            @RequestParam(defaultValue = "10") int topN,
            @RequestParam(required = false) Long libraryId) {
        try {
            List<Reservation> reservations = reservationMapper.selectList(null);

            // 按用户统计预约和履约情况
            Map<Long, long[]> userStats = new HashMap<>();
            for (Reservation r : reservations) {
                if (r == null || r.getUserId() == null || r.getStatus() == null) continue;
                if (libraryId != null && !libraryId.equals(r.getLibraryId())) continue;
                userStats.computeIfAbsent(r.getUserId(), k -> new long[3]); // total, completed, noShow
                userStats.get(r.getUserId())[0]++; // total
                if ("已使用".equals(r.getStatus()) || "已完成".equals(r.getStatus()) || "暂离".equals(r.getStatus())) {
                    userStats.get(r.getUserId())[1]++; // completed（签到/使用/暂离均视为履约）
                } else if ("爽约".equals(r.getStatus())) {
                    userStats.get(r.getUserId())[2]++; // noShow
                }
            }

            List<Map<String, Object>> result = userStats.entrySet().stream()
                    .filter(e -> e.getValue()[0] >= 3) // 至少3次预约才计入排行
                    .map(e -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("userId", e.getKey());
                        // 查询用户真实姓名
                        User user = userMapper.selectById(e.getKey());
                        item.put("realName", user != null ? user.getRealName() : ("用户" + e.getKey()));
                        item.put("total", e.getValue()[0]);
                        item.put("completed", e.getValue()[1]);
                        item.put("noShow", e.getValue()[2]);
                        double rate = e.getValue()[0] > 0 ?
                                (double) e.getValue()[1] / e.getValue()[0] * 100 : 0;
                        item.put("complianceRate", String.format("%.1f", rate));
                        return item;
                    })
                    .sorted((a, b) -> Double.compare(
                            Double.parseDouble((String) b.get("complianceRate")),
                            Double.parseDouble((String) a.get("complianceRate"))))
                    .limit(topN)
                    .collect(Collectors.toList());

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("用户行为查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取近12个月预约趋势
     */
    @GetMapping("/monthly-trend")
    public Result<List<Map<String, Object>>> getMonthlyTrend(@RequestParam(required = false) Long libraryId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            List<Reservation> reservations = reservationMapper.selectList(null);
            LocalDate now = LocalDate.now();

            for (int i = 11; i >= 0; i--) {
                LocalDate date = now.minusMonths(i);
                int year = date.getYear();
                int month = date.getMonthValue();

                long count = reservations.stream()
                        .filter(r -> {
                            if (r == null || r.getStartTime() == null) return false;
                            if (libraryId != null && !libraryId.equals(r.getLibraryId())) return false;
                            LocalDateTime start = r.getStartTime();
                            return start.getYear() == year && start.getMonthValue() == month;
                        })
                        .count();

                Map<String, Object> item = new HashMap<>();
                item.put("month", year + "-" + String.format("%02d", month));
                item.put("count", count);
                result.add(item);
            }

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("月度趋势查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取最受欢迎座位（按预约次数排序）
     */
    @GetMapping("/popular-seats")
    public Result<List<Map<String, Object>>> getPopularSeats(
            @RequestParam(required = false) Long libraryId,
            @RequestParam(defaultValue = "10") int topN) {
        try {
            List<Reservation> reservations = reservationMapper.selectList(null);

            // 按座位ID分组统计预约次数（只统计已使用/已完成的有效记录）
            Map<Long, long[]> seatStats = new HashMap<>();
            for (Reservation r : reservations) {
                if (r == null || r.getSeatId() == null || r.getStatus() == null) continue;
                // 排除取消/爽约的无效记录
                if ("已取消".equals(r.getStatus()) || "爽约".equals(r.getStatus())) continue;
                if (libraryId != null && !libraryId.equals(r.getLibraryId())) continue;

                seatStats.computeIfAbsent(r.getSeatId(), k -> new long[1]);
                seatStats.get(r.getSeatId())[0]++;
            }

            List<Map<String, Object>> result = seatStats.entrySet().stream()
                    .filter(e -> e.getValue()[0] > 0)
                    .map(e -> {
                        Map<String, Object> item = new HashMap<>();
                        Long seatId = e.getKey();
                        item.put("seatId", seatId);
                        item.put("reservationCount", e.getValue()[0]);

                        // 查询座位详情
                        Seat seat = seatMapper.selectById(seatId);
                        if (seat != null) {
                            item.put("seatNumber", seat.getSeatNumber());
                            item.put("seatType", seat.getSeatType());
                            item.put("rowNum", seat.getRowNum());
                            item.put("colNum", seat.getColNum());
                            item.put("seatLibraryId", seat.getLibraryId());

                            // 查询图书室名称
                            com.library.entity.Library lib = libraryMapper.selectById(seat.getLibraryId());
                            item.put("libraryName", lib != null ? lib.getName() : "未知");
                        }
                        return item;
                    })
                    .sorted((a, b) -> Long.compare(
                            (Long) b.get("reservationCount"),
                            (Long) a.get("reservationCount")))
                    .limit(topN)
                    .collect(Collectors.toList());

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("热门座位查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取近7天预约趋势
     */
    @GetMapping("/reservation-trend")
    public Result<List<Map<String, Object>>> getReservationTrend(@RequestParam(required = false) Long libraryId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 获取近7天数据
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                LocalDateTime dayStart = date.atStartOfDay();
                LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
                
                List<Reservation> allReservations = reservationMapper.selectList(null);
                
                long count = allReservations.stream()
                    .filter(r -> {
                        if (r == null || r.getStartTime() == null) {
                            return false;
                        }
                        LocalDateTime startTime = r.getStartTime();
                        boolean matchDate = !startTime.isBefore(dayStart) && 
                                           startTime.isBefore(dayEnd);
                        
                        if (!matchDate) return false;
                        
                        if (libraryId != null) {
                            return libraryId.equals(r.getLibraryId());
                        }
                        return true;
                    })
                    .count();
                
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", date.toString());
                dayData.put("count", count);
                
                result.add(dayData);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预约趋势查询失败: " + e.getMessage());
        }
    }
}
