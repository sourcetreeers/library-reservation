package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.Announcement;
import com.library.entity.Banner;
import com.library.entity.Reservation;
import com.library.entity.Seat;
import com.library.mapper.LibraryMapper;
import com.library.mapper.ReservationMapper;
import com.library.mapper.SeatMapper;
import com.library.service.AnnouncementService;
import com.library.service.BannerService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 公告和轮播图控制器
 */
@RestController
@RequestMapping("/common")
@CrossOrigin
public class CommonController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private BannerService bannerService;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private LibraryMapper libraryMapper;
    
    /**
     * 获取发布中的公告列表
     */
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        List<Announcement> announcements = announcementService.getActiveAnnouncements();
        return Result.success(announcements);
    }
    
    /**
     * 获取启用的轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success(banners);
    }

    /**
     * 获取热门推荐座位（移动端公开接口）
     * 返回指定图书馆预约次数最多的座位ID列表
     */
    @GetMapping("/popular-seats")
    public Result<Map<String, Object>> getPopularSeats(@RequestParam Long libraryId, @RequestParam(defaultValue = "5") int topN) {
        try {
            // 查询该图书馆所有座位
            LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
            seatWrapper.eq(Seat::getLibraryId, libraryId);
            List<Seat> seats = seatMapper.selectList(seatWrapper);

            // 按座位ID统计有效预约次数
            List<Reservation> allReservations = reservationMapper.selectList(null);
            Map<Long, Long> seatCountMap = allReservations.stream()
                    .filter(r -> r != null && r.getSeatId() != null && r.getStatus() != null
                            && libraryId.equals(r.getLibraryId())
                            && !"已取消".equals(r.getStatus()) && !"爽约".equals(r.getStatus()))
                    .collect(Collectors.groupingBy(Reservation::getSeatId, Collectors.counting()));

            // 排序取 TopN
            List<Long> hotSeatIds = seatCountMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(topN)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("libraryId", libraryId);
            result.put("hotSeatIds", hotSeatIds);

            // 同时返回带详情的热门列表（供前端展示推荐信息）
            List<Map<String, Object>> hotSeatsDetail = new ArrayList<>();
            for (Long seatId : hotSeatIds) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("seatId", seatId);
                detail.put("reservationCount", seatCountMap.getOrDefault(seatId, 0L));

                Seat seat = seatMapper.selectById(seatId);
                if (seat != null) {
                    detail.put("seatNumber", seat.getSeatNumber());
                    detail.put("seatType", seat.getSeatType());
                }
                hotSeatsDetail.add(detail);
            }
            result.put("hotSeats", hotSeatsDetail);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取热门座位失败: " + e.getMessage());
        }
    }
}
