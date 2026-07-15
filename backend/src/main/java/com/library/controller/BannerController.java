package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.aspect.Log;
import com.library.entity.Banner;
import com.library.entity.User;
import com.library.service.BannerService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 轮播图管理控制器
 */
@RestController
@RequestMapping("/admin/banner")
@CrossOrigin
public class BannerController {
    
    @Autowired
    private BannerService bannerService;
    
    /**
     * 分页查询轮播图列表
     */
    @GetMapping("/page")
    public Result<Page<Banner>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        
        Page<Banner> page = new Page<>(current, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(Banner::getTitle, title);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Banner::getStatus, status);
        }
        
        wrapper.orderByAsc(Banner::getSortOrder)
               .orderByDesc(Banner::getCreateTime);
        
        Page<Banner> result = bannerService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 新增轮播图
     */
    @Log(value = "新增", module = "轮播图")
    @PostMapping
    public Result<Object> addBanner(@RequestBody Banner banner, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        bannerService.save(banner);
        return Result.success("添加成功");
    }
    
    /**
     * 更新轮播图
     */
    @Log(value = "修改", module = "轮播图")
    @PutMapping
    public Result<Object> updateBanner(@RequestBody Banner banner, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        bannerService.updateById(banner);
        return Result.success("更新成功");
    }
    
    /**
     * 删除轮播图
     */
    @Log(value = "删除", module = "轮播图")
    @DeleteMapping("/{id}")
    public Result<Object> deleteBanner(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        bannerService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 启用/禁用轮播图
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
        
        Banner banner = bannerService.getById(id);
        if (banner != null) {
            banner.setStatus("启用".equals(banner.getStatus()) ? "禁用" : "启用");
            bannerService.updateById(banner);
        }
        
        return Result.success("操作成功");
    }
}
