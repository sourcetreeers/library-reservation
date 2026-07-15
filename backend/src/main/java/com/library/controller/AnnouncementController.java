package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.aspect.Log;
import com.library.entity.Announcement;
import com.library.entity.User;
import com.library.service.AnnouncementService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/admin/announcement")
@CrossOrigin
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 分页查询公告列表
     */
    @GetMapping("/page")
    public Result<Page<Announcement>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        
        Page<Announcement> page = new Page<>(current, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(Announcement::getTitle, title);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Announcement::getStatus, status);
        }
        
        wrapper.orderByDesc(Announcement::getPriority)
               .orderByDesc(Announcement::getCreateTime);
        
        Page<Announcement> result = announcementService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 新增公告
     */
    @Log(value = "新增", module = "公告")
    @PostMapping
    public Result<Object> addAnnouncement(@RequestBody Announcement announcement, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        announcement.setCreateBy(user.getUsername());
        announcement.setPublishTime(LocalDateTime.now());
        announcementService.save(announcement);
        
        return Result.success("发布成功");
    }
    
    /**
     * 更新公告
     */
    @Log(value = "修改", module = "公告")
    @PutMapping
    public Result<Object> updateAnnouncement(@RequestBody Announcement announcement, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        announcementService.updateById(announcement);
        return Result.success("更新成功");
    }
    
    /**
     * 删除公告
     */
    @Log(value = "删除", module = "公告")
    @DeleteMapping("/{id}")
    public Result<Object> deleteAnnouncement(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        announcementService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 上下架公告
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Object> toggleStatus(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        Announcement announcement = announcementService.getById(id);
        if (announcement != null) {
            announcement.setStatus("发布中".equals(announcement.getStatus()) ? "已下架" : "发布中");
            announcementService.updateById(announcement);
        }
        
        return Result.success("操作成功");
    }
}
