package com.library.controller;

import com.library.entity.Appeal;
import com.library.entity.User;
import com.library.service.AppealService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 申诉控制器
 */
@RestController
@RequestMapping("/appeal")
@CrossOrigin
public class AppealController {

    @Autowired
    private AppealService appealService;

    /**
     * 学生发起申诉
     */
    @PostMapping
    public Result<Object> createAppeal(@RequestBody Map<String, Object> params, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }

        Long pointsRecordId = Long.valueOf(params.get("pointsRecordId").toString());
        String reason = (String) params.get("reason");
        String imageUrl = (String) params.get("imageUrl");

        if (reason == null || reason.trim().isEmpty()) {
            return Result.error("请填写申诉理由");
        }

        try {
            appealService.createAppeal(user.getId(), pointsRecordId, reason, imageUrl);
            return Result.success("申诉提交成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看自己的申诉记录
     */
    @GetMapping("/my")
    public Result<PageResult<Appeal>> getMyAppeals(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        PageResult<Appeal> result = appealService.getUserAppeals(user.getId(), current, size);
        return Result.success(result);
    }

    /**
     * 管理员分页查询所有申诉
     */
    @GetMapping("/page")
    public Result<PageResult<Appeal>> getAppealPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限访问");
        }
        PageResult<Appeal> result = appealService.getAppealPage(status, current, size);
        return Result.success(result);
    }

    /**
     * 管理员处理申诉
     */
    @PutMapping("/{id}/handle")
    public Result<Object> handleAppeal(@PathVariable Long id,
                                       @RequestBody Map<String, String> params,
                                       HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }

        String status = params.get("status");
        String reply = params.get("reply");

        if (status == null || (!"已通过".equals(status) && !"已驳回".equals(status))) {
            return Result.error("状态值不正确");
        }

        try {
            appealService.handleAppeal(id, user.getId(), status, reply);
            return Result.success("处理成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
