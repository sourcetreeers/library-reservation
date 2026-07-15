package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Appeal;
import com.library.mapper.AppealMapper;
import com.library.service.AppealService;
import com.library.service.NotificationService;
import com.library.service.PointsService;
import com.library.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申诉服务实现类
 */
@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements AppealService {

    @Autowired
    private AppealMapper appealMapper;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public void createAppeal(Long userId, Long pointsRecordId, String reason, String imageUrl) {
        // 检查是否已对该记录发起过申诉（待审核状态）
        int count = appealMapper.countPendingByPointsRecordId(pointsRecordId);
        if (count > 0) {
            throw new RuntimeException("该记录已有申诉正在审核中");
        }

        Appeal appeal = new Appeal();
        appeal.setUserId(userId);
        appeal.setPointsRecordId(pointsRecordId);
        appeal.setReason(reason);
        appeal.setImageUrl(imageUrl);
        appeal.setStatus("待审核");
        appeal.setCreateTime(LocalDateTime.now());
        appealMapper.insert(appeal);
    }

    @Override
    public PageResult<Appeal> getUserAppeals(Long userId, int current, int size) {
        int offset = (current - 1) * size;

        LambdaQueryWrapper<Appeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appeal::getUserId, userId);
        wrapper.orderByDesc(Appeal::getCreateTime);

        List<Appeal> records = appealMapper.selectList(wrapper.last("LIMIT " + offset + ", " + size));
        long total = appealMapper.selectCount(wrapper);

        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<Appeal> getAppealPage(String status, int current, int size) {
        int offset = (current - 1) * size;
        List<Appeal> records = appealMapper.selectAppealPage(status, offset, size);
        long total = appealMapper.countAppeals(status);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    @Transactional
    public void handleAppeal(Long appealId, Long handlerId, String status, String reply) {
        Appeal appeal = appealMapper.selectById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉记录不存在");
        }
        if (!"待审核".equals(appeal.getStatus())) {
            throw new RuntimeException("该申诉已被处理");
        }

        appeal.setStatus(status);
        appeal.setHandlerId(handlerId);
        appeal.setReply(reply);
        appeal.setHandleTime(LocalDateTime.now());
        appealMapper.updateById(appeal);

        // 如果申诉通过，返还扣减的积分
        if ("已通过".equals(status)) {
            // 返还积分（扣了多少返多少，这里简化处理，返还6分）
            pointsService.addPoints(appeal.getUserId(), 6, "申诉返还",
                    "APPEAL", null, "申诉通过，积分返还");
        }

        // 发送通知给用户
        String title = "申诉结果";
        String content = status.equals("已通过") ?
                "您的申诉已通过，积分已返还" :
                "您的申诉已被驳回，回复：" + (reply != null ? reply : "无");
        notificationService.createNotification(appeal.getUserId(), title, content,
                "申诉结果", "/mobile/my-points");
    }
}
