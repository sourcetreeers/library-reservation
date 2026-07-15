package com.library.controller;

import com.library.dto.DisputeQuery;
import com.library.dto.DisputeVO;
import com.library.dto.HandleDisputeDTO;
import com.library.aspect.Log;
import com.library.entity.User;
import com.library.service.DisputeService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 争议统一处理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/disputes")
@CrossOrigin
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    /**
     * 管理员分页查询所有争议（合并申诉+举报）
     */
    @GetMapping
    public Result<PageResult<DisputeVO>> getDisputePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限访问");
        }

        DisputeQuery query = new DisputeQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setType(type);
        query.setStatus(status);

        PageResult<DisputeVO> result = disputeService.getDisputePage(query);
        return Result.success(result);
    }

    /**
     * 管理员处理争议
     */
    @Log(value = "处理", module = "争议")
    @PutMapping("/{id}/handle")
    public Result<Object> handleDispute(@PathVariable Long id,
                                        @RequestBody HandleDisputeDTO dto,
                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限操作");
        }

        try {
            disputeService.handleDispute(id, user.getId(), dto);
            return Result.success("处理成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
