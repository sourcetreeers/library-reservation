package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Announcement;
import com.library.mapper.AnnouncementMapper;
import com.library.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告Service实现
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Override
    public List<Announcement> getActiveAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, "发布中")
               .le(Announcement::getPublishTime, LocalDateTime.now()) // 已到发布时间
               .or()
               .isNull(Announcement::getPublishTime) // 或者没有设置发布时间
               .orderByDesc(Announcement::getPriority) // 按优先级降序
               .orderByDesc(Announcement::getCreateTime); // 再按创建时间降序
        
        List<Announcement> announcements = list(wrapper);
        
        // 过滤已过期的公告
        LocalDateTime now = LocalDateTime.now();
        return announcements.stream()
            .filter(a -> a.getExpireTime() == null || a.getExpireTime().isAfter(now))
            .collect(java.util.stream.Collectors.toList());
    }
}
