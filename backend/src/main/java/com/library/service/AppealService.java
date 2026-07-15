package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Appeal;
import com.library.vo.PageResult;

/**
 * 申诉服务接口
 */
public interface AppealService extends IService<Appeal> {

    /**
     * 学生发起申诉
     */
    void createAppeal(Long userId, Long pointsRecordId, String reason, String imageUrl);

    /**
     * 获取用户的申诉记录
     */
    PageResult<Appeal> getUserAppeals(Long userId, int current, int size);

    /**
     * 管理员分页查询所有申诉
     */
    PageResult<Appeal> getAppealPage(String status, int current, int size);

    /**
     * 管理员处理申诉
     */
    void handleAppeal(Long appealId, Long handlerId, String status, String reply);
}
