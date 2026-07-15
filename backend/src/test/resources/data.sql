-- 测试数据

-- 用户数据 (密码: admin123 -> MD5: 21232f297a57a5a743894a0e4a801fc3, 123456 -> e10adc3949ba59abbe56e057f20f883e)
INSERT INTO "user" (id, username, password, real_name, phone, email, user_type, status) VALUES
(1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', NULL, NULL, '图书馆管理员', '正常'),
(2, 'student001', 'e10adc3949ba59abbe56e057f20f883e', '张三', NULL, NULL, '学生', '正常'),
(3, 'student002', 'e10adc3949ba59abbe56e057f20f883e', '李四', NULL, NULL, '学生', '正常'),
(6, 'system_admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@library.com', '系统管理员', '正常');

-- 图书馆数据
INSERT INTO library (id, name, address, description, status) VALUES
(1, '中央图书室', '校园中心区域', '主要图书室，藏书丰富', '正常'),
(2, '理工图书室', '校图书馆一楼东侧', '专业图书室，主要收藏理工类书籍', '正常'),
(3, '文学图书室', '校图书馆一楼西侧', '文学类专业图书室', '正常');

-- 座位数据
INSERT INTO seat (id, library_id, seat_number, seat_type, status, row_num, col_num) VALUES
(1, 1, '001', '普通座位', '正常', 1, 1),
(2, 1, '002', '普通座位', '正常', 1, 2),
(3, 1, '003', '电脑座位', '正常', 1, 3),
(4, 1, '004', '普通座位', '维修', 1, 4),
(5, 2, '001', '普通座位', '正常', 1, 1),
(6, 2, '002', '电脑座位', '正常', 1, 2);

-- 预约规则
INSERT INTO reservation_rule (id, rule_key, rule_name, rule_value, description) VALUES
(1, 'reservation_time_slots', '可预约时间段', '07:00-22:00', '每天可预约的时间段'),
(2, 'max_daily_reservations', '单日最大预约次数', '2', '每个用户每天最多可以预约的次数'),
(3, 'max_reservation_hours', '单次最长预约时长', '4', '每次预约的最长时长（小时）'),
(4, 'temp_leave_timeout', '暂离超时时间', '20', '暂离状态超时分钟数');

-- 用户积分
INSERT INTO user_points (id, user_id, student_no, total_points, current_points, credit_level, max_reservation_hours, max_reservation_count) VALUES
(1, 2, 'student001', 80, 80, '良好', 4, 3),
(2, 3, 'student002', 86, 70, '一般', 2, 2);

-- 系统配置
INSERT INTO system_config (id, config_key, config_value, description) VALUES
(1, 'daily_order_sequence', '0', '每日订单序列号');

-- 预约记录（用于查询测试）
INSERT INTO reservation (id, order_no, user_id, library_id, seat_id, start_time, end_time, status) VALUES
(1, '20260316-0001', 2, 1, 2, '2026-03-17 08:00:00', '2026-03-17 12:00:00', '已取消'),
(2, '20260316-0002', 2, 1, 3, '2026-06-29 08:00:00', '2026-06-29 12:00:00', '已预约');
