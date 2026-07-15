package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Complaint;
import com.library.vo.PageResult;

/**
 * 举报服务接口
 */
public interface ComplaintService extends IService<Complaint> {

    /**
     * 学生提交举报
     */
    void createComplaint(Long userId, Long reservationId, String description, String imageUrl);

    /**
     * 获取用户的举报记录
     */
    PageResult<Complaint> getUserComplaints(Long userId, int current, int size);

    /**
     * 管理员分页查询所有举报
     */
    PageResult<Complaint> getComplaintPage(String status, int current, int size);

    /**
     * 管理员处理举报
     */
    void handleComplaint(Long complaintId, Long handlerId, String status, String reply);
}
