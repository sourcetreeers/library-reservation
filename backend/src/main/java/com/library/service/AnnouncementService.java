package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Announcement;
import java.util.List;

/**
 * 公告Service
 */
public interface AnnouncementService extends IService<Announcement> {
    
    /**
     * 获取发布中的公告列表（按优先级排序）
     */
    List<Announcement> getActiveAnnouncements();
}
