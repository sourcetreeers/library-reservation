package com.library.controller;

import com.library.entity.Complaint;
import com.library.entity.User;
import com.library.service.ComplaintService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 举报控制器
 */
@RestController
@RequestMapping("/complaint")
@CrossOrigin
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    /**
     * 学生提交举报
     */
    @PostMapping
    public Result<Object> createComplaint(@RequestBody Map<String, Object> params, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }

        Long reservationId = Long.valueOf(params.get("reservationId").toString());
        String description = (String) params.get("description");
        String imageUrl = (String) params.get("imageUrl");

        if (description == null || description.trim().isEmpty()) {
            return Result.error("请填写举报描述");
        }

        try {
            complaintService.createComplaint(user.getId(), reservationId, description, imageUrl);
            return Result.success("举报提交成功，请等待管理员审核");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看自己的举报记录
     */
    @GetMapping("/my")
    public Result<PageResult<Complaint>> getMyComplaints(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        PageResult<Complaint> result = complaintService.getUserComplaints(user.getId(), current, size);
        return Result.success(result);
    }

    /**
     * 管理员分页查询所有举报
     */
    @GetMapping("/admin/page")
    public Result<PageResult<Complaint>> getComplaintPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限访问");
        }
        PageResult<Complaint> result = complaintService.getComplaintPage(status, current, size);
        return Result.success(result);
    }

    /**
     * 管理员处理举报
     */
    @PutMapping("/{id}/handle")
    public Result<Object> handleComplaint(@PathVariable Long id,
                                          @RequestBody Map<String, String> params,
                                          HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }

        String status = params.get("status");
        String reply = params.get("reply");

        if (status == null || (!"已确认".equals(status) && !"已驳回".equals(status) && !"仅返还".equals(status))) {
            return Result.error("状态值不正确");
        }

        try {
            complaintService.handleComplaint(id, user.getId(), status, reply);
            return Result.success("处理成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
