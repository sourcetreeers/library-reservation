package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 通知消息Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 获取用户未读通知数量
     */
    @Select("SELECT COUNT(1) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    long countUnread(@Param("userId") Long userId);

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    void markAllAsRead(@Param("userId") Long userId);
}
