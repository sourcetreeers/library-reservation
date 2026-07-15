/*
 Navicat Premium Dump SQL

 Source Server         : springboot3
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : library_reservation

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 29/05/2026 17:38:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '普通公告' COMMENT '公告类型：普通公告/维修通知/紧急通知',
  `priority` int NULL DEFAULT 0 COMMENT '优先级：数字越大越靠前',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '发布中' COMMENT '状态：发布中/已下架',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '中央图书室维修通知', '中央图书室今日维修中，请预约的同学取消预约，给您带来不便敬请谅解。', '维修通知', 30, '发布中', '2026-05-05 19:21:29', NULL, NULL, '2026-05-05 19:21:29', '2026-05-05 19:21:29');
INSERT INTO `announcement` VALUES (2, '图书馆开放时间调整', '因节假日安排，图书馆开放时间临时调整为8:00-20:00。', '普通公告', 5, '发布中', '2026-05-05 19:21:29', NULL, NULL, '2026-05-05 19:21:29', '2026-05-05 19:21:29');
INSERT INTO `announcement` VALUES (3, '期末考试期间座位预约规则调整', '期末考试期间，每人每天最多可预约4小时，请合理安排学习时间。', '紧急通知', 8, '发布中', '2026-05-05 19:21:29', NULL, NULL, '2026-05-05 19:21:29', '2026-05-05 19:21:29');

-- ----------------------------
-- Table structure for appeal
-- ----------------------------
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points_record_id` bigint NOT NULL COMMENT '关联积分记录ID',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申诉理由',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申诉证据图片URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '待审核' COMMENT '状态：待审核/已通过/已驳回',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理回复',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_points_record_id`(`points_record_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '违约申诉表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appeal
-- ----------------------------

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '轮播图标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序：数字越小越靠前',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '启用' COMMENT '状态：启用/禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '欢迎使用图书馆预约系统', '/images/欢迎使用图书馆预约系统.png', '/mobile/home', 1, '启用', '2026-05-05 19:21:29', '2026-05-06 17:07:39');
INSERT INTO `banner` VALUES (2, '期末考试加油', '/images/期末考试加油.png', '/mobile/home', 2, '启用', '2026-05-05 19:21:29', '2026-05-06 17:07:48');
INSERT INTO `banner` VALUES (3, '图书馆使用指南', '/images/图书馆使用指南.png', '/mobile/home', 3, '启用', '2026-05-05 19:21:29', '2026-05-06 17:07:28');

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `reporter_id` bigint NOT NULL COMMENT '举报人ID（被占座的学生）',
  `occupant_id` bigint NULL DEFAULT NULL COMMENT '被举报人ID（占座的学生，系统自动查询或管理员填写）',
  `seat_id` bigint NOT NULL COMMENT '座位ID',
  `library_id` bigint NOT NULL COMMENT '图书室ID',
  `reporter_reservation_id` bigint NOT NULL COMMENT '举报人的预约ID（用于恢复积分）',
  `occupant_reservation_id` bigint NULL DEFAULT NULL COMMENT '占座者的预约ID（系统自动查询）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '举报描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '待处理' COMMENT '状态：待处理/已确认/已驳回',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID（管理员）',
  `handler_reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理回复',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reporter_id`(`reporter_id` ASC) USING BTREE,
  INDEX `idx_occupant_id`(`occupant_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_reporter_reservation_id`(`reporter_reservation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '占座举报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of complaint
-- ----------------------------
INSERT INTO `complaint` VALUES (1, 8, 4, 1, 1, 13, 10, '被占座位了啊啊啊', '仅返还', 1, NULL, '2026-05-17 02:29:07', '2026-05-17 16:46:45');
INSERT INTO `complaint` VALUES (2, 2, NULL, 2, 1, 11, NULL, '1', '已确认', 1, NULL, '2026-05-17 20:07:35', '2026-05-17 21:12:54');
INSERT INTO `complaint` VALUES (3, 8, NULL, 6, 1, 19, NULL, '11', '已驳回', 1, '11', '2026-05-20 15:18:27', '2026-05-20 15:19:59');

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '提交人ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提交人姓名（冗余）',
  `student_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号（冗余）',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈类型：建议/投诉/报修/其他',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈内容',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL（可选）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待处理' COMMENT '状态：待处理/已回复',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handler_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理人姓名（冗余）',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '管理员回复内容',
  `handle_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理结果：已采纳/已驳回/已转交',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 9, '李飘', NULL, '问题反馈', '中央图书室空调问腿', '空调不凉啊', NULL, '已回复', 1, '管理员', '马上派人处理', '已采纳', '2026-05-17 16:37:34', '2026-05-17 16:38:33', NULL);
INSERT INTO `feedback` VALUES (2, 8, 'yyy', NULL, '问题反馈', '空调', '中央图书室空调有问题！！', NULL, '已回复', 1, '管理员', '11', '已采纳', '2026-05-20 14:11:20', '2026-05-20 15:22:46', NULL);

-- ----------------------------
-- Table structure for library
-- ----------------------------
DROP TABLE IF EXISTS `library`;
CREATE TABLE `library`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书室ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图书室名称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '状态: 正常, 停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '图书馆表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of library
-- ----------------------------
INSERT INTO `library` VALUES (1, '中央图书室', '校园中心区域', '主要图书室，藏书丰富', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:18');
INSERT INTO `library` VALUES (2, '理工图书室', '校图书馆一楼东侧', '专业图书室，主要收藏理工类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:23');
INSERT INTO `library` VALUES (3, '文学图书室', '校图书馆一楼西侧', '文学类专业图书室，收藏文史哲书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:25');
INSERT INTO `library` VALUES (4, '医学图书室', '校图书馆一楼南侧', '医学专业图书室，收藏医学类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:29');
INSERT INTO `library` VALUES (5, '法学图书室', '校图书馆一楼北侧', '法学专业图书室，收藏法律类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:33');
INSERT INTO `library` VALUES (6, '经管图书室', '校图书馆二楼东侧', '经济管理类专业图书室', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:41');
INSERT INTO `library` VALUES (7, '外语图书室', '校图书馆二楼西侧', '外语类专业图书室，多语种书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:43');
INSERT INTO `library` VALUES (8, '艺术图书室', '校图书馆二楼南侧', '艺术类专业图书室，收藏艺术设计书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:45');
INSERT INTO `library` VALUES (9, '体育图书室', '校图书馆二楼北侧', '体育类专业图书室', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:48');
INSERT INTO `library` VALUES (10, '音乐图书室', '校图书馆三楼东侧', '音乐类专业图书室，收藏音乐理论书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:50');
INSERT INTO `library` VALUES (11, '化学图书室', '校图书馆三楼西侧', '化学专业图书室，收藏化学类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:54');
INSERT INTO `library` VALUES (12, '物理图书室', '校图书馆三楼南侧', '物理专业图书室，收藏物理类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:56');
INSERT INTO `library` VALUES (13, '数学图书室', '校图书馆三楼北侧', '数学专业图书室，收藏数学类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:38:58');
INSERT INTO `library` VALUES (14, '计算机图书室', '校图书馆四楼东侧', '计算机专业图书室，收藏IT类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:00');
INSERT INTO `library` VALUES (15, '建筑图书室', '校图书馆四楼西侧', '建筑专业图书室，收藏建筑设计书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:01');
INSERT INTO `library` VALUES (16, '环境图书室', '校图书馆四楼南侧', '环境科学专业图书室', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:03');
INSERT INTO `library` VALUES (17, '生物图书室', '校图书馆四楼北侧', '生物专业图书室，收藏生物类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:07');
INSERT INTO `library` VALUES (18, '地理图书室', '校图书馆五楼一室', '地理专业图书室，收藏地理类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:09');
INSERT INTO `library` VALUES (19, '历史图书室', '校图书馆五楼二室', '历史专业图书室，收藏历史类书籍', '正常', '2026-03-16 14:25:22', '2026-03-16 20:39:12');
INSERT INTO `library` VALUES (20, '哲学图书室', '校图书馆五楼三室', '哲学专业图书室，收藏哲学类书籍', '停用', '2026-03-16 14:25:22', '2026-03-16 20:39:28');
INSERT INTO `library` VALUES (21, '图书1', '1', '1', '停用', '2026-05-16 01:01:09', '2026-05-16 01:01:09');
INSERT INTO `library` VALUES (22, '2', '2', '2', '正常', '2026-05-16 15:19:38', '2026-05-16 15:19:38');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知内容',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：预约提醒/扣分通知/加分通知/申诉结果/系统公告',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `link_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联链接',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 9, '预约即将开始', '您有预约将于 15:00 开始，座位编号：1，请按时签到', '预约提醒', 1, '/mobile/my-reservations', '2026-05-17 14:50:00');
INSERT INTO `notification` VALUES (2, 9, '预约即将开始', '您有预约将于 15:00 开始，座位编号：1，请按时签到', '预约提醒', 1, '/mobile/my-reservations', '2026-05-17 14:55:00');
INSERT INTO `notification` VALUES (3, 9, '积分增加', '按时签到履约', '加分通知', 1, '/mobile/my-points', '2026-05-17 14:57:25');
INSERT INTO `notification` VALUES (4, 9, '您的反馈已回复', '您提交的《中央图书室空调问腿》已收到回复，请查看。', '反馈通知', 1, '/mobile/my-feedbacks', '2026-05-17 16:38:33');
INSERT INTO `notification` VALUES (5, 8, '积分返还', '您的举报已审核通过，因占座问题被扣除的积分已返还', '系统通知', 1, '/mobile/my-points', '2026-05-17 16:46:45');
INSERT INTO `notification` VALUES (6, 8, '举报处理结果', '您的举报已审核，积分已返还。', '系统通知', 1, '/mobile/my-points', '2026-05-17 16:46:45');
INSERT INTO `notification` VALUES (7, 2, '积分返还', '您的举报已审核通过，因占座问题被扣除的积分已返还', '系统通知', 1, '/mobile/my-points', '2026-05-17 21:12:54');
INSERT INTO `notification` VALUES (8, 2, '举报处理结果', '您的举报已审核确认，占座者已被扣分，您的积分已返还。', '系统通知', 1, '/mobile/my-points', '2026-05-17 21:12:54');
INSERT INTO `notification` VALUES (9, 8, '预约即将开始', '您有预约将于 13:31 开始，座位编号：9，请按时签到', '预约提醒', 1, '/mobile/my-reservations', '2026-05-18 13:30:00');
INSERT INTO `notification` VALUES (10, 8, '积分扣减', '预约后未签到，视为爽约', '扣分通知', 1, '/mobile/my-points', '2026-05-18 13:35:00');
INSERT INTO `notification` VALUES (11, 8, '积分扣减', '预约后未签到，视为爽约', '扣分通知', 1, '/mobile/my-points', '2026-05-18 13:50:00');
INSERT INTO `notification` VALUES (12, 8, '积分扣减', '预约后未签到，视为爽约', '扣分通知', 1, '/mobile/my-points', '2026-05-18 14:20:00');
INSERT INTO `notification` VALUES (13, 9, '积分增加', '按时签到履约', '加分通知', 1, '/mobile/my-points', '2026-05-18 14:45:44');
INSERT INTO `notification` VALUES (14, 8, '举报处理结果', '您的举报已被驳回，回复：11', '系统通知', 1, '/mobile/my-points', '2026-05-20 15:19:59');
INSERT INTO `notification` VALUES (15, 8, '您的反馈已回复', '您提交的《空调》已收到回复，请查看。', '反馈通知', 1, '/mobile/my-feedbacks', '2026-05-20 15:22:46');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operate_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人用户名',
  `operate_user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operate_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型：登录/新增/修改/删除/查询/导出/其他',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作对象类型：如图书室/座位/用户/公告/规则等',
  `target_id` bigint NULL DEFAULT NULL COMMENT '操作对象ID',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作详情（JSON格式，记录操作前后数据快照）',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器UA',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operate_user`(`operate_user` ASC) USING BTREE,
  INDEX `idx_operate_type`(`operate_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_target_type`(`target_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:47:22');
INSERT INTO `operation_log` VALUES (2, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:47:40');
INSERT INTO `operation_log` VALUES (3, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:48:21');
INSERT INTO `operation_log` VALUES (4, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:58:27');
INSERT INTO `operation_log` VALUES (5, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:59:01');
INSERT INTO `operation_log` VALUES (6, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 22:59:40');
INSERT INTO `operation_log` VALUES (7, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:05:52');
INSERT INTO `operation_log` VALUES (8, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:09:33');
INSERT INTO `operation_log` VALUES (9, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:09:45');
INSERT INTO `operation_log` VALUES (10, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:09:49');
INSERT INTO `operation_log` VALUES (11, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:12:20');
INSERT INTO `operation_log` VALUES (12, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:12:23');
INSERT INTO `operation_log` VALUES (13, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:19:16');
INSERT INTO `operation_log` VALUES (14, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:36');
INSERT INTO `operation_log` VALUES (15, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:38');
INSERT INTO `operation_log` VALUES (16, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:38');
INSERT INTO `operation_log` VALUES (17, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:38');
INSERT INTO `operation_log` VALUES (18, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:40');
INSERT INTO `operation_log` VALUES (19, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:41');
INSERT INTO `operation_log` VALUES (20, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:41');
INSERT INTO `operation_log` VALUES (21, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:20:45');
INSERT INTO `operation_log` VALUES (22, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-17 23:24:47');
INSERT INTO `operation_log` VALUES (23, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36', '2026-05-17 23:53:31');
INSERT INTO `operation_log` VALUES (24, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 00:01:54');
INSERT INTO `operation_log` VALUES (25, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36', '2026-05-18 00:05:46');
INSERT INTO `operation_log` VALUES (26, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 00:17:09');
INSERT INTO `operation_log` VALUES (27, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 00:54:17');
INSERT INTO `operation_log` VALUES (28, 'admin', 1, '新增', '座位', NULL, '{\"method\":\"save\",\"success\":true,\"params\":\"[{\\\"id\\\":1086,\\\"libraryId\\\":1,\\\"seatNumber\\\":\\\"134\\\",\\\"seatType\\\":\\\"普通座位\\\",\\\"status\\\":\\\"正常\\\",\\\"rowNum\\\":2,\\\"colNum\\\":1,\\\"createTime\\\":null,\\\"updateTime\\\":null},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 00:54:39');
INSERT INTO `operation_log` VALUES (29, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 00:54:45');
INSERT INTO `operation_log` VALUES (30, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:04:46');
INSERT INTO `operation_log` VALUES (31, 'admin', 1, '修改', '图书室', NULL, '{\"method\":\"update\",\"success\":true,\"params\":\"[21,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:05:42');
INSERT INTO `operation_log` VALUES (32, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:05:57');
INSERT INTO `operation_log` VALUES (33, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:06:25');
INSERT INTO `operation_log` VALUES (34, 'admin', 1, '导出', '用户列表', NULL, '{\"method\":\"exportUsers\",\"success\":true,\"params\":\"[]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:06:33');
INSERT INTO `operation_log` VALUES (35, 'admin', 1, '删除', '座位', NULL, '{\"method\":\"delete\",\"success\":true,\"params\":\"[1086,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:06:41');
INSERT INTO `operation_log` VALUES (36, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:06:47');
INSERT INTO `operation_log` VALUES (37, 'system_admin', 6, '删除', '违规规则', NULL, '{\"method\":\"deleteRule\",\"success\":true,\"params\":\"[7,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:07:40');
INSERT INTO `operation_log` VALUES (38, 'system_admin', 6, '删除', '违规规则', NULL, '{\"method\":\"deleteRule\",\"success\":true,\"params\":\"[8,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:07:42');
INSERT INTO `operation_log` VALUES (39, 'system_admin', 6, '修改', '用户', NULL, '{\"method\":\"toggleStatus\",\"success\":true,\"params\":\"[9,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:11:20');
INSERT INTO `operation_log` VALUES (40, 'system_admin', 6, '修改', '用户', NULL, '{\"method\":\"toggleStatus\",\"success\":true,\"params\":\"[9,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 01:11:22');
INSERT INTO `operation_log` VALUES (41, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '2026-05-18 01:19:20');
INSERT INTO `operation_log` VALUES (42, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 01:19:46');
INSERT INTO `operation_log` VALUES (43, 'yyy', 8, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 01:20:20');
INSERT INTO `operation_log` VALUES (44, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 13:03:27');
INSERT INTO `operation_log` VALUES (45, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 13:24:56');
INSERT INTO `operation_log` VALUES (46, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 13:29:05');
INSERT INTO `operation_log` VALUES (47, 'yyy', 8, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 13:29:28');
INSERT INTO `operation_log` VALUES (48, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 13:47:10');
INSERT INTO `operation_log` VALUES (49, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 13:47:12');
INSERT INTO `operation_log` VALUES (50, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 13:47:20');
INSERT INTO `operation_log` VALUES (51, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 13:48:05');
INSERT INTO `operation_log` VALUES (52, 'yyy', 8, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 13:48:45');
INSERT INTO `operation_log` VALUES (53, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:01:58');
INSERT INTO `operation_log` VALUES (54, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:07:21');
INSERT INTO `operation_log` VALUES (55, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:11:50');
INSERT INTO `operation_log` VALUES (56, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:16:47');
INSERT INTO `operation_log` VALUES (57, 'yyy', 8, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:17:17');
INSERT INTO `operation_log` VALUES (58, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:20:24');
INSERT INTO `operation_log` VALUES (59, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:24:42');
INSERT INTO `operation_log` VALUES (60, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:31:30');
INSERT INTO `operation_log` VALUES (61, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:35:55');
INSERT INTO `operation_log` VALUES (62, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:41:44');
INSERT INTO `operation_log` VALUES (63, 'yyy', 8, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:42:59');
INSERT INTO `operation_log` VALUES (64, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:44:28');
INSERT INTO `operation_log` VALUES (65, 'lp', 9, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:45:34');
INSERT INTO `operation_log` VALUES (66, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-18 14:47:29');
INSERT INTO `operation_log` VALUES (67, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:49:29');
INSERT INTO `operation_log` VALUES (68, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:54:39');
INSERT INTO `operation_log` VALUES (69, 'system_admin', 6, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 14:57:27');
INSERT INTO `operation_log` VALUES (70, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 15:11:06');
INSERT INTO `operation_log` VALUES (71, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 16:02:36');
INSERT INTO `operation_log` VALUES (72, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-18 16:50:55');
INSERT INTO `operation_log` VALUES (73, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-19 09:38:36');
INSERT INTO `operation_log` VALUES (74, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-19 16:20:30');
INSERT INTO `operation_log` VALUES (75, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-19 16:21:43');
INSERT INTO `operation_log` VALUES (76, '未知用户', NULL, '新增', '预约', NULL, '{\"method\":\"create\",\"success\":true,\"params\":\"[]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-19 17:12:59');
INSERT INTO `operation_log` VALUES (77, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '2026-05-19 23:50:46');
INSERT INTO `operation_log` VALUES (78, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-19 23:51:17');
INSERT INTO `operation_log` VALUES (79, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 01:54:48');
INSERT INTO `operation_log` VALUES (80, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 01:59:19');
INSERT INTO `operation_log` VALUES (81, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 12:59:13');
INSERT INTO `operation_log` VALUES (82, 'lp', 9, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 12:59:27');
INSERT INTO `operation_log` VALUES (83, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 13:00:27');
INSERT INTO `operation_log` VALUES (84, 'admin', 1, '删除', '座位', NULL, '{\"method\":\"delete\",\"success\":true,\"params\":\"[1084,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 13:01:45');
INSERT INTO `operation_log` VALUES (85, 'admin', 1, '删除', '座位', NULL, '{\"method\":\"delete\",\"success\":true,\"params\":\"[1085,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 13:01:45');
INSERT INTO `operation_log` VALUES (86, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 14:02:05');
INSERT INTO `operation_log` VALUES (87, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 14:02:23');
INSERT INTO `operation_log` VALUES (88, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 14:11:35');
INSERT INTO `operation_log` VALUES (89, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 14:14:01');
INSERT INTO `operation_log` VALUES (90, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 14:46:21');
INSERT INTO `operation_log` VALUES (91, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 14:46:25');
INSERT INTO `operation_log` VALUES (92, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 14:57:57');
INSERT INTO `operation_log` VALUES (93, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 15:01:36');
INSERT INTO `operation_log` VALUES (94, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 15:07:05');
INSERT INTO `operation_log` VALUES (95, 'lp', 9, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"lp\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 15:17:47');
INSERT INTO `operation_log` VALUES (96, 'lp', 9, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"yyy\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '4.2.2.2,127.0.0.1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36 Edg/148.0.0.0', '2026-05-20 15:18:08');
INSERT INTO `operation_log` VALUES (97, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:18:31');
INSERT INTO `operation_log` VALUES (98, 'admin', 1, '处理', '争议', NULL, '{\"method\":\"handleDispute\",\"success\":true,\"params\":\"[3,{\\\"type\\\":\\\"complaint\\\",\\\"status\\\":\\\"已驳回\\\",\\\"reply\\\":\\\"11\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:19:59');
INSERT INTO `operation_log` VALUES (99, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:20:35');
INSERT INTO `operation_log` VALUES (100, '未知用户', NULL, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"admin\\\",\\\"password\\\":\\\"admin\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:21:17');
INSERT INTO `operation_log` VALUES (101, 'admin', 1, '导出', '违规记录', NULL, '{\"method\":\"exportViolations\",\"success\":true,\"params\":\"[null,]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:23:30');
INSERT INTO `operation_log` VALUES (102, 'admin', 1, '登录', '系统', NULL, '{\"method\":\"login\",\"success\":true,\"params\":\"[{\\\"username\\\":\\\"system_admin\\\",\\\"password\\\":\\\"123456\\\"},]\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '2026-05-20 15:24:45');

-- ----------------------------
-- Table structure for points_record
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学工号',
  `points_change` int NOT NULL COMMENT '积分变动（正数为加分，负数为减分）',
  `points_before` int NOT NULL COMMENT '变动前积分',
  `points_after` int NOT NULL COMMENT '变动后积分',
  `change_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '变动类型：正常履约/提前签退/爽约/暂离超时/未签退离馆/系统调整',
  `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源类型：预约签到/预约签退/系统检测/管理员操作',
  `reservation_id` bigint NULL DEFAULT NULL COMMENT '关联的预约ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_reservation_id`(`reservation_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_change_type`(`change_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分变动记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of points_record
-- ----------------------------
INSERT INTO `points_record` VALUES (1, 2, 'student001', 1, 60, 61, '正常履约', '预约签到', 3, '按时签到履约', '2026-05-05 16:51:50');
INSERT INTO `points_record` VALUES (2, 3, 'student002', -6, 80, 74, '爽约', '系统检测', 5, '预约后未签到，视为爽约', '2026-05-05 22:00:53');
INSERT INTO `points_record` VALUES (3, 2, 'student001', -6, 81, 75, '爽约', '系统检测', 4, '预约后未签到，视为爽约', '2026-05-06 08:00:00');
INSERT INTO `points_record` VALUES (4, 7, 'lisi', 1, 80, 81, '正常履约', '预约签到', 6, '按时签到履约', '2026-05-06 16:43:22');
INSERT INTO `points_record` VALUES (5, 4, 'wangwu', -6, 80, 74, '爽约', '系统检测', 7, '预约后未签到，视为爽约', '2026-05-09 11:00:00');
INSERT INTO `points_record` VALUES (6, 2, 'student001', -6, 75, 69, '爽约', '系统检测', 8, '预约后未签到，视为爽约', '2026-05-12 19:00:00');
INSERT INTO `points_record` VALUES (7, 4, 'wangwu', -6, 74, 68, '爽约', '系统检测', 9, '预约后未签到，视为爽约', '2026-05-13 09:00:00');
INSERT INTO `points_record` VALUES (8, 4, 'wangwu', 1, 68, 69, '正常履约', '预约签到', 10, '按时签到履约', '2026-05-13 09:07:08');
INSERT INTO `points_record` VALUES (9, 2, 'student001', -6, 69, 63, '爽约', '系统检测', 11, '预约后未签到，视为爽约', '2026-05-13 09:20:00');
INSERT INTO `points_record` VALUES (10, 8, 'yyy', -6, 80, 74, '爽约', '系统检测', 13, '预约后未签到，视为爽约', '2026-05-16 08:30:00');
INSERT INTO `points_record` VALUES (11, 8, 'yyy', 1, 74, 75, '正常履约', '预约签到', 14, '按时签到履约', '2026-05-16 12:56:36');
INSERT INTO `points_record` VALUES (12, 9, 'lp', 1, 80, 81, '正常履约', '预约签到', 15, '按时签到履约', '2026-05-16 15:18:03');
INSERT INTO `points_record` VALUES (13, 9, 'lp', 1, 81, 82, '正常履约', '预约签到', 16, '按时签到履约', '2026-05-17 14:57:25');
INSERT INTO `points_record` VALUES (14, 8, 'yyy', 6, 80, 86, '系统调整', '管理员操作', 13, '占座举报审核通过，返还积分', '2026-05-17 16:46:45');
INSERT INTO `points_record` VALUES (15, 2, 'student001', 6, 80, 86, '系统调整', '管理员操作', 11, '占座举报审核通过，返还积分', '2026-05-17 21:12:54');
INSERT INTO `points_record` VALUES (16, 8, 'yyy', -6, 86, 80, '爽约', '系统检测', 17, '预约后未签到，视为爽约', '2026-05-18 13:35:00');
INSERT INTO `points_record` VALUES (17, 8, 'yyy', -6, 80, 74, '爽约', '系统检测', 18, '预约后未签到，视为爽约', '2026-05-18 13:50:00');
INSERT INTO `points_record` VALUES (18, 8, 'yyy', -6, 74, 68, '爽约', '系统检测', 19, '预约后未签到，视为爽约', '2026-05-18 14:20:00');
INSERT INTO `points_record` VALUES (19, 9, 'lp', 1, 82, 83, '正常履约', '预约签到', 21, '按时签到履约', '2026-05-18 14:45:44');

-- ----------------------------
-- Table structure for punishment_rule
-- ----------------------------
DROP TABLE IF EXISTS `punishment_rule`;
CREATE TABLE `punishment_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `violation_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '违规类型：爽约/暂离超时/未签退离馆/恶意占座',
  `apply_times` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用次数：FIRST=第1次/SECOND=第2次/THIRD_PLUS=第3次及以后',
  `points_deduct` int NOT NULL DEFAULT 0 COMMENT '扣分值（负数表示扣分）',
  `ban_days` int NOT NULL DEFAULT 0 COMMENT '封禁天数（0表示不封禁）',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1启用 0停用',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规则描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_violation_apply`(`violation_type` ASC, `apply_times` ASC, `is_active` ASC) USING BTREE,
  INDEX `idx_violation_type`(`violation_type` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '违规处罚规则配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of punishment_rule
-- ----------------------------
INSERT INTO `punishment_rule` VALUES (1, '爽约', 'FIRST', -5, 0, 1, '首次爽约警告', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (2, '爽约', 'SECOND', -10, 1, 1, '第2次爽约扣分并封禁1天', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (3, '爽约', 'THIRD_PLUS', -20, 3, 1, '第3次及以上爽约加重处罚', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (4, '暂离超时', 'FIRST', -3, 0, 1, '首次暂离超时警告', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (5, '暂离超时', 'SECOND', -5, 0, 1, '第2次暂离超时扣分', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (6, '暂离超时', 'THIRD_PLUS', -10, 1, 1, '第3次及以上暂离超时封禁1天', '2026-05-17 16:26:35', '2026-05-17 16:26:35');
INSERT INTO `punishment_rule` VALUES (9, '恶意占座', 'FIRST', -10, 0, 1, '', NULL, NULL);
INSERT INTO `punishment_rule` VALUES (10, '恶意占座', 'SECOND', -20, 3, 1, '', NULL, NULL);

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `order_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约流水号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `library_id` bigint NOT NULL COMMENT '图书馆ID',
  `seat_id` bigint NOT NULL COMMENT '座位ID',
  `start_time` datetime NOT NULL COMMENT '预约开始时间',
  `end_time` datetime NOT NULL COMMENT '预约结束时间',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已预约' COMMENT '状态: 已预约, 已使用, 爽约, 已取消',
  `qr_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '二维码内容',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_library_id`(`library_id` ASC) USING BTREE,
  INDEX `idx_seat_id`(`seat_id` ASC) USING BTREE,
  INDEX `idx_start_end_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_reservation_library` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reservation_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reservation
-- ----------------------------
INSERT INTO `reservation` VALUES (1, '20260316-0001', 2, 1, 2, '2026-03-17 08:00:00', '2026-03-17 12:00:00', '已取消', '20260316-0001', NULL, '2026-03-16 14:41:54', '2026-03-16 14:41:54');
INSERT INTO `reservation` VALUES (2, '20260316-0002', 4, 2, 51, '2026-03-17 08:00:00', '2026-03-17 12:00:00', '已使用', '20260316-0002', '2026-03-16 20:32:43', '2026-03-16 17:50:12', '2026-03-16 17:50:12');
INSERT INTO `reservation` VALUES (3, '20260504-0003', 2, 1, 1, '2026-05-05 08:00:00', '2026-05-05 22:00:00', '已使用', '20260504-0003', '2026-05-05 16:51:50', '2026-05-04 21:39:46', '2026-05-04 21:39:46');
INSERT INTO `reservation` VALUES (4, '20260505-0004', 2, 1, 2, '2026-05-06 08:00:00', '2026-05-06 22:00:00', '爽约', '20260505-0004', NULL, '2026-05-05 17:02:06', '2026-05-05 17:02:06');
INSERT INTO `reservation` VALUES (5, '20260505-0005', 3, 3, 101, '2026-05-05 21:00:00', '2026-05-05 22:00:00', '爽约', '20260505-0005', NULL, '2026-05-05 20:38:55', '2026-05-05 20:38:55');
INSERT INTO `reservation` VALUES (6, '20260506-0006', 7, 2, 51, '2026-05-06 17:00:00', '2026-05-06 18:00:00', '已使用', '20260506-0006', '2026-05-06 16:43:22', '2026-05-06 16:37:49', '2026-05-06 16:37:49');
INSERT INTO `reservation` VALUES (7, '20260508-0007', 4, 1, 1, '2026-05-09 11:00:00', '2026-05-09 13:00:00', '爽约', '20260508-0007', NULL, '2026-05-08 23:32:09', '2026-05-08 23:32:09');
INSERT INTO `reservation` VALUES (8, '20260512-0008', 2, 1, 3, '2026-05-12 19:00:00', '2026-05-12 20:00:00', '爽约', '20260512-0008', NULL, '2026-05-12 18:05:28', '2026-05-12 18:05:28');
INSERT INTO `reservation` VALUES (9, '20260513-0009', 4, 1, 1, '2026-05-13 09:00:00', '2026-05-13 10:00:00', '爽约', '20260513-0009', NULL, '2026-05-13 08:49:26', '2026-05-13 08:49:26');
INSERT INTO `reservation` VALUES (10, '20260513-0010', 4, 1, 1, '2026-05-13 09:10:00', '2026-05-13 10:00:00', '已使用', '20260513-0010', '2026-05-13 09:07:08', '2026-05-13 09:06:54', '2026-05-13 09:06:54');
INSERT INTO `reservation` VALUES (11, '20260513-0011', 2, 1, 2, '2026-05-13 09:19:00', '2026-05-13 10:00:00', '爽约', '20260513-0011', NULL, '2026-05-13 09:09:25', '2026-05-13 09:09:25');
INSERT INTO `reservation` VALUES (12, '20260515-0012', 8, 1, 2, '2026-05-15 08:04:00', '2026-05-15 12:03:00', '已取消', '20260515-0012', NULL, '2026-05-15 03:06:32', '2026-05-15 03:06:32');
INSERT INTO `reservation` VALUES (13, '20260516-0013', 8, 1, 1, '2026-05-16 08:30:00', '2026-05-16 10:00:00', '爽约', '20260516-0013', NULL, '2026-05-16 00:59:40', '2026-05-16 00:59:40');
INSERT INTO `reservation` VALUES (14, '20260516-0014', 8, 3, 101, '2026-05-16 13:00:00', '2026-05-16 14:00:00', '已使用', '20260516-0014', '2026-05-16 12:56:36', '2026-05-16 12:55:57', '2026-05-16 12:55:57');
INSERT INTO `reservation` VALUES (15, '20260516-0015', 9, 4, 151, '2026-05-16 15:22:00', '2026-05-16 17:00:00', '已使用', '20260516-0015', '2026-05-16 15:18:03', '2026-05-16 15:17:34', '2026-05-16 15:17:34');
INSERT INTO `reservation` VALUES (16, '20260517-0016', 9, 1, 1, '2026-05-17 15:00:00', '2026-05-17 17:00:00', '已使用', '20260517-0016', '2026-05-17 14:57:25', '2026-05-17 14:47:45', '2026-05-17 14:47:45');
INSERT INTO `reservation` VALUES (17, '20260518-0017', 8, 1, 9, '2026-05-18 13:31:00', '2026-05-18 15:00:00', '爽约', '20260518-0017', NULL, '2026-05-18 13:29:27', '2026-05-18 13:29:27');
INSERT INTO `reservation` VALUES (18, '20260518-0018', 8, 1, 2, '2026-05-18 13:50:00', '2026-05-18 15:00:00', '爽约', '20260518-0018', NULL, '2026-05-18 13:48:44', '2026-05-18 13:48:44');
INSERT INTO `reservation` VALUES (19, '20260518-0019', 8, 1, 6, '2026-05-18 14:18:00', '2026-05-18 16:00:00', '爽约', '20260518-0019', NULL, '2026-05-18 14:17:17', '2026-05-18 14:17:17');
INSERT INTO `reservation` VALUES (20, '20260518-0020', 8, 1, 9, '2026-05-24 07:00:00', '2026-05-24 08:00:00', '已预约', '20260518-0020', NULL, '2026-05-18 14:42:58', '2026-05-18 14:42:58');
INSERT INTO `reservation` VALUES (21, '20260518-0021', 9, 1, 1, '2026-05-18 14:46:00', '2026-05-18 16:00:00', '已使用', '20260518-0021', '2026-05-18 14:45:44', '2026-05-18 14:45:34', '2026-05-18 14:45:34');

-- ----------------------------
-- Table structure for reservation_rule
-- ----------------------------
DROP TABLE IF EXISTS `reservation_rule`;
CREATE TABLE `reservation_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rule_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则键名',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
  `rule_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_key`(`rule_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约规则配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation_rule
-- ----------------------------
INSERT INTO `reservation_rule` VALUES (1, 'reservation_time_slots', '可预约时间段', '07:00-22:00', '每天可预约的时间段，多个时间段用逗号分隔', '2026-05-17 03:22:32', '2026-05-17 11:55:09');
INSERT INTO `reservation_rule` VALUES (2, 'max_daily_reservations', '单日最大预约次数', '2', '每个用户每天最多可以预约的次数', '2026-05-17 03:22:32', '2026-05-17 03:22:32');
INSERT INTO `reservation_rule` VALUES (3, 'max_reservation_hours', '单次最长预约时长', '4', '每次预约的最长时长（小时）', '2026-05-17 03:22:32', '2026-05-17 03:22:32');
INSERT INTO `reservation_rule` VALUES (4, 'temp_leave_timeout', '暂离超时时间', '20', '暂离状态超过此分钟数将自动签退并扣分（考试周可调整）', '2026-05-17 03:22:32', '2026-05-17 03:22:32');

-- ----------------------------
-- Table structure for seat
-- ----------------------------
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '座位ID',
  `library_id` bigint NOT NULL COMMENT '图书馆ID',
  `seat_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '座位编号',
  `seat_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通座位' COMMENT '座位类型',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '状态: 正常, 维修, 停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `row_num` int NULL DEFAULT 0 COMMENT '行号（座位平面图行位置）',
  `col_num` int NULL DEFAULT 0 COMMENT '列号（座位平面图列位置）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_library_seat`(`library_id` ASC, `seat_number` ASC) USING BTREE,
  INDEX `idx_library_id`(`library_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_seat_library` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1087 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '座位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seat
-- ----------------------------
INSERT INTO `seat` VALUES (1, 1, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 1, 1);
INSERT INTO `seat` VALUES (2, 1, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 1, 2);
INSERT INTO `seat` VALUES (3, 1, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 1, 3);
INSERT INTO `seat` VALUES (4, 1, '004', '普通座位', '维修', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 1, 4);
INSERT INTO `seat` VALUES (5, 1, '005', '普通座位', '停用', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 2, 1);
INSERT INTO `seat` VALUES (6, 1, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 2, 2);
INSERT INTO `seat` VALUES (7, 1, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 2, 3);
INSERT INTO `seat` VALUES (8, 1, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 2, 4);
INSERT INTO `seat` VALUES (9, 1, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 3, 1);
INSERT INTO `seat` VALUES (10, 1, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 3, 2);
INSERT INTO `seat` VALUES (11, 1, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 3, 3);
INSERT INTO `seat` VALUES (12, 1, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 3, 4);
INSERT INTO `seat` VALUES (13, 1, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 4, 1);
INSERT INTO `seat` VALUES (14, 1, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 4, 2);
INSERT INTO `seat` VALUES (15, 1, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 4, 3);
INSERT INTO `seat` VALUES (16, 1, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 4, 4);
INSERT INTO `seat` VALUES (17, 1, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 5, 1);
INSERT INTO `seat` VALUES (18, 1, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 5, 2);
INSERT INTO `seat` VALUES (19, 1, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 5, 3);
INSERT INTO `seat` VALUES (20, 1, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 5, 4);
INSERT INTO `seat` VALUES (21, 1, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 6, 1);
INSERT INTO `seat` VALUES (22, 1, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 6, 2);
INSERT INTO `seat` VALUES (23, 1, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 6, 3);
INSERT INTO `seat` VALUES (24, 1, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 6, 4);
INSERT INTO `seat` VALUES (25, 1, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 7, 1);
INSERT INTO `seat` VALUES (26, 1, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 7, 2);
INSERT INTO `seat` VALUES (27, 1, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 7, 3);
INSERT INTO `seat` VALUES (28, 1, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 7, 4);
INSERT INTO `seat` VALUES (29, 1, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 8, 1);
INSERT INTO `seat` VALUES (30, 1, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 8, 2);
INSERT INTO `seat` VALUES (31, 1, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 8, 3);
INSERT INTO `seat` VALUES (32, 1, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 8, 4);
INSERT INTO `seat` VALUES (33, 1, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 9, 1);
INSERT INTO `seat` VALUES (34, 1, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 9, 2);
INSERT INTO `seat` VALUES (35, 1, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 9, 3);
INSERT INTO `seat` VALUES (36, 1, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 9, 4);
INSERT INTO `seat` VALUES (37, 1, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 10, 1);
INSERT INTO `seat` VALUES (38, 1, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 10, 2);
INSERT INTO `seat` VALUES (39, 1, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 10, 3);
INSERT INTO `seat` VALUES (40, 1, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 10, 4);
INSERT INTO `seat` VALUES (41, 1, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 11, 1);
INSERT INTO `seat` VALUES (42, 1, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 11, 2);
INSERT INTO `seat` VALUES (43, 1, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 11, 3);
INSERT INTO `seat` VALUES (44, 1, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 11, 4);
INSERT INTO `seat` VALUES (45, 1, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 12, 1);
INSERT INTO `seat` VALUES (46, 1, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 12, 2);
INSERT INTO `seat` VALUES (47, 1, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 12, 3);
INSERT INTO `seat` VALUES (48, 1, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 12, 4);
INSERT INTO `seat` VALUES (49, 1, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 13, 1);
INSERT INTO `seat` VALUES (50, 1, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 13, 2);
INSERT INTO `seat` VALUES (51, 2, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 13, 3);
INSERT INTO `seat` VALUES (52, 2, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 13, 4);
INSERT INTO `seat` VALUES (53, 2, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 14, 1);
INSERT INTO `seat` VALUES (54, 2, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 14, 2);
INSERT INTO `seat` VALUES (55, 2, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 14, 3);
INSERT INTO `seat` VALUES (56, 2, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 14, 4);
INSERT INTO `seat` VALUES (57, 2, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 15, 1);
INSERT INTO `seat` VALUES (58, 2, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 15, 2);
INSERT INTO `seat` VALUES (59, 2, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 15, 3);
INSERT INTO `seat` VALUES (60, 2, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 15, 4);
INSERT INTO `seat` VALUES (61, 2, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 16, 1);
INSERT INTO `seat` VALUES (62, 2, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 16, 2);
INSERT INTO `seat` VALUES (63, 2, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 16, 3);
INSERT INTO `seat` VALUES (64, 2, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 16, 4);
INSERT INTO `seat` VALUES (65, 2, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 17, 1);
INSERT INTO `seat` VALUES (66, 2, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 17, 2);
INSERT INTO `seat` VALUES (67, 2, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 17, 3);
INSERT INTO `seat` VALUES (68, 2, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 17, 4);
INSERT INTO `seat` VALUES (69, 2, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 18, 1);
INSERT INTO `seat` VALUES (70, 2, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 18, 2);
INSERT INTO `seat` VALUES (71, 2, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 18, 3);
INSERT INTO `seat` VALUES (72, 2, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 18, 4);
INSERT INTO `seat` VALUES (73, 2, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 19, 1);
INSERT INTO `seat` VALUES (74, 2, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 19, 2);
INSERT INTO `seat` VALUES (75, 2, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 19, 3);
INSERT INTO `seat` VALUES (76, 2, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 19, 4);
INSERT INTO `seat` VALUES (77, 2, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 20, 1);
INSERT INTO `seat` VALUES (78, 2, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 20, 2);
INSERT INTO `seat` VALUES (79, 2, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 20, 3);
INSERT INTO `seat` VALUES (80, 2, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 20, 4);
INSERT INTO `seat` VALUES (81, 2, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 21, 1);
INSERT INTO `seat` VALUES (82, 2, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 21, 2);
INSERT INTO `seat` VALUES (83, 2, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 21, 3);
INSERT INTO `seat` VALUES (84, 2, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 21, 4);
INSERT INTO `seat` VALUES (85, 2, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 22, 1);
INSERT INTO `seat` VALUES (86, 2, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 22, 2);
INSERT INTO `seat` VALUES (87, 2, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 22, 3);
INSERT INTO `seat` VALUES (88, 2, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 22, 4);
INSERT INTO `seat` VALUES (89, 2, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 23, 1);
INSERT INTO `seat` VALUES (90, 2, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 23, 2);
INSERT INTO `seat` VALUES (91, 2, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 23, 3);
INSERT INTO `seat` VALUES (92, 2, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 23, 4);
INSERT INTO `seat` VALUES (93, 2, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 24, 1);
INSERT INTO `seat` VALUES (94, 2, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 24, 2);
INSERT INTO `seat` VALUES (95, 2, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 24, 3);
INSERT INTO `seat` VALUES (96, 2, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 24, 4);
INSERT INTO `seat` VALUES (97, 2, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 25, 1);
INSERT INTO `seat` VALUES (98, 2, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 25, 2);
INSERT INTO `seat` VALUES (99, 2, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 25, 3);
INSERT INTO `seat` VALUES (100, 2, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 25, 4);
INSERT INTO `seat` VALUES (101, 3, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 26, 1);
INSERT INTO `seat` VALUES (102, 3, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 26, 2);
INSERT INTO `seat` VALUES (103, 3, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 26, 3);
INSERT INTO `seat` VALUES (104, 3, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 26, 4);
INSERT INTO `seat` VALUES (105, 3, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 27, 1);
INSERT INTO `seat` VALUES (106, 3, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 27, 2);
INSERT INTO `seat` VALUES (107, 3, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 27, 3);
INSERT INTO `seat` VALUES (108, 3, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 27, 4);
INSERT INTO `seat` VALUES (109, 3, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 28, 1);
INSERT INTO `seat` VALUES (110, 3, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 28, 2);
INSERT INTO `seat` VALUES (111, 3, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 28, 3);
INSERT INTO `seat` VALUES (112, 3, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 28, 4);
INSERT INTO `seat` VALUES (113, 3, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 29, 1);
INSERT INTO `seat` VALUES (114, 3, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 29, 2);
INSERT INTO `seat` VALUES (115, 3, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 29, 3);
INSERT INTO `seat` VALUES (116, 3, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 29, 4);
INSERT INTO `seat` VALUES (117, 3, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 30, 1);
INSERT INTO `seat` VALUES (118, 3, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 30, 2);
INSERT INTO `seat` VALUES (119, 3, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 30, 3);
INSERT INTO `seat` VALUES (120, 3, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 30, 4);
INSERT INTO `seat` VALUES (121, 3, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 31, 1);
INSERT INTO `seat` VALUES (122, 3, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 31, 2);
INSERT INTO `seat` VALUES (123, 3, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 31, 3);
INSERT INTO `seat` VALUES (124, 3, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 31, 4);
INSERT INTO `seat` VALUES (125, 3, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 32, 1);
INSERT INTO `seat` VALUES (126, 3, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 32, 2);
INSERT INTO `seat` VALUES (127, 3, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 32, 3);
INSERT INTO `seat` VALUES (128, 3, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 32, 4);
INSERT INTO `seat` VALUES (129, 3, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 33, 1);
INSERT INTO `seat` VALUES (130, 3, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 33, 2);
INSERT INTO `seat` VALUES (131, 3, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 33, 3);
INSERT INTO `seat` VALUES (132, 3, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 33, 4);
INSERT INTO `seat` VALUES (133, 3, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 34, 1);
INSERT INTO `seat` VALUES (134, 3, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 34, 2);
INSERT INTO `seat` VALUES (135, 3, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 34, 3);
INSERT INTO `seat` VALUES (136, 3, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 34, 4);
INSERT INTO `seat` VALUES (137, 3, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 35, 1);
INSERT INTO `seat` VALUES (138, 3, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 35, 2);
INSERT INTO `seat` VALUES (139, 3, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 35, 3);
INSERT INTO `seat` VALUES (140, 3, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 35, 4);
INSERT INTO `seat` VALUES (141, 3, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 36, 1);
INSERT INTO `seat` VALUES (142, 3, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 36, 2);
INSERT INTO `seat` VALUES (143, 3, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 36, 3);
INSERT INTO `seat` VALUES (144, 3, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 36, 4);
INSERT INTO `seat` VALUES (145, 3, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 37, 1);
INSERT INTO `seat` VALUES (146, 3, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 37, 2);
INSERT INTO `seat` VALUES (147, 3, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 37, 3);
INSERT INTO `seat` VALUES (148, 3, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 37, 4);
INSERT INTO `seat` VALUES (149, 3, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 38, 1);
INSERT INTO `seat` VALUES (150, 3, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 38, 2);
INSERT INTO `seat` VALUES (151, 4, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 38, 3);
INSERT INTO `seat` VALUES (152, 4, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 38, 4);
INSERT INTO `seat` VALUES (153, 4, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 39, 1);
INSERT INTO `seat` VALUES (154, 4, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 39, 2);
INSERT INTO `seat` VALUES (155, 4, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 39, 3);
INSERT INTO `seat` VALUES (156, 4, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 39, 4);
INSERT INTO `seat` VALUES (157, 4, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 40, 1);
INSERT INTO `seat` VALUES (158, 4, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 40, 2);
INSERT INTO `seat` VALUES (159, 4, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 40, 3);
INSERT INTO `seat` VALUES (160, 4, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 40, 4);
INSERT INTO `seat` VALUES (161, 4, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 41, 1);
INSERT INTO `seat` VALUES (162, 4, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 41, 2);
INSERT INTO `seat` VALUES (163, 4, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 41, 3);
INSERT INTO `seat` VALUES (164, 4, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 41, 4);
INSERT INTO `seat` VALUES (165, 4, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 42, 1);
INSERT INTO `seat` VALUES (166, 4, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 42, 2);
INSERT INTO `seat` VALUES (167, 4, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 42, 3);
INSERT INTO `seat` VALUES (168, 4, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 42, 4);
INSERT INTO `seat` VALUES (169, 4, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 43, 1);
INSERT INTO `seat` VALUES (170, 4, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 43, 2);
INSERT INTO `seat` VALUES (171, 4, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 43, 3);
INSERT INTO `seat` VALUES (172, 4, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 43, 4);
INSERT INTO `seat` VALUES (173, 4, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 44, 1);
INSERT INTO `seat` VALUES (174, 4, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 44, 2);
INSERT INTO `seat` VALUES (175, 4, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 44, 3);
INSERT INTO `seat` VALUES (176, 4, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 44, 4);
INSERT INTO `seat` VALUES (177, 4, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 45, 1);
INSERT INTO `seat` VALUES (178, 4, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 45, 2);
INSERT INTO `seat` VALUES (179, 4, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 45, 3);
INSERT INTO `seat` VALUES (180, 4, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 45, 4);
INSERT INTO `seat` VALUES (181, 4, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 46, 1);
INSERT INTO `seat` VALUES (182, 4, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 46, 2);
INSERT INTO `seat` VALUES (183, 4, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 46, 3);
INSERT INTO `seat` VALUES (184, 4, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 46, 4);
INSERT INTO `seat` VALUES (185, 4, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 47, 1);
INSERT INTO `seat` VALUES (186, 4, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 47, 2);
INSERT INTO `seat` VALUES (187, 4, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 47, 3);
INSERT INTO `seat` VALUES (188, 4, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 47, 4);
INSERT INTO `seat` VALUES (189, 4, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 48, 1);
INSERT INTO `seat` VALUES (190, 4, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 48, 2);
INSERT INTO `seat` VALUES (191, 4, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 48, 3);
INSERT INTO `seat` VALUES (192, 4, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 48, 4);
INSERT INTO `seat` VALUES (193, 4, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 49, 1);
INSERT INTO `seat` VALUES (194, 4, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 49, 2);
INSERT INTO `seat` VALUES (195, 4, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 49, 3);
INSERT INTO `seat` VALUES (196, 4, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 49, 4);
INSERT INTO `seat` VALUES (197, 4, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 50, 1);
INSERT INTO `seat` VALUES (198, 4, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 50, 2);
INSERT INTO `seat` VALUES (199, 4, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 50, 3);
INSERT INTO `seat` VALUES (200, 4, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 01:08:27', 50, 4);
INSERT INTO `seat` VALUES (201, 5, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (202, 5, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (203, 5, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (204, 5, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (205, 5, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (206, 5, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (207, 5, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (208, 5, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (209, 5, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (210, 5, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (211, 5, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (212, 5, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (213, 5, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (214, 5, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (215, 5, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (216, 5, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (217, 5, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (218, 5, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (219, 5, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (220, 5, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (221, 5, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (222, 5, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (223, 5, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (224, 5, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (225, 5, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (226, 5, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (227, 5, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (228, 5, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (229, 5, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (230, 5, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (231, 5, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (232, 5, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (233, 5, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (234, 5, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (235, 5, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (236, 5, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (237, 5, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (238, 5, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (239, 5, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (240, 5, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (241, 5, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (242, 5, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (243, 5, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (244, 5, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (245, 5, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (246, 5, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (247, 5, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (248, 5, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (249, 5, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (250, 5, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (251, 6, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (252, 6, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (253, 6, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (254, 6, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (255, 6, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (256, 6, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (257, 6, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (258, 6, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (259, 6, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (260, 6, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (261, 6, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (262, 6, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (263, 6, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (264, 6, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (265, 6, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (266, 6, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (267, 6, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (268, 6, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (269, 6, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (270, 6, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (271, 6, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (272, 6, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (273, 6, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (274, 6, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (275, 6, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (276, 6, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (277, 6, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (278, 6, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (279, 6, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (280, 6, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (281, 6, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (282, 6, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (283, 6, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (284, 6, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (285, 6, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (286, 6, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (287, 6, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (288, 6, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (289, 6, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (290, 6, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (291, 6, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (292, 6, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (293, 6, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (294, 6, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (295, 6, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (296, 6, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (297, 6, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (298, 6, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (299, 6, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (300, 6, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (301, 7, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (302, 7, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (303, 7, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (304, 7, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (305, 7, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (306, 7, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (307, 7, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (308, 7, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (309, 7, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (310, 7, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (311, 7, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (312, 7, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (313, 7, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (314, 7, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (315, 7, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (316, 7, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (317, 7, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (318, 7, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (319, 7, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (320, 7, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (321, 7, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (322, 7, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (323, 7, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (324, 7, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (325, 7, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (326, 7, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (327, 7, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (328, 7, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (329, 7, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (330, 7, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (331, 7, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (332, 7, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (333, 7, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (334, 7, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (335, 7, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (336, 7, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (337, 7, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (338, 7, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (339, 7, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (340, 7, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (341, 7, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (342, 7, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (343, 7, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (344, 7, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (345, 7, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (346, 7, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (347, 7, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (348, 7, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (349, 7, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (350, 7, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (351, 8, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (352, 8, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (353, 8, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (354, 8, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (355, 8, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (356, 8, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (357, 8, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (358, 8, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (359, 8, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (360, 8, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (361, 8, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (362, 8, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (363, 8, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (364, 8, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (365, 8, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (366, 8, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (367, 8, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (368, 8, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (369, 8, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (370, 8, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (371, 8, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (372, 8, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (373, 8, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (374, 8, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (375, 8, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (376, 8, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (377, 8, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (378, 8, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (379, 8, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (380, 8, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (381, 8, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (382, 8, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (383, 8, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (384, 8, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (385, 8, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (386, 8, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (387, 8, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (388, 8, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (389, 8, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (390, 8, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (391, 8, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (392, 8, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (393, 8, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (394, 8, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (395, 8, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (396, 8, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (397, 8, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (398, 8, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (399, 8, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (400, 8, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (401, 9, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (402, 9, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (403, 9, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (404, 9, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (405, 9, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (406, 9, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (407, 9, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (408, 9, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (409, 9, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (410, 9, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (411, 9, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (412, 9, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (413, 9, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (414, 9, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (415, 9, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (416, 9, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (417, 9, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (418, 9, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (419, 9, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (420, 9, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (421, 9, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (422, 9, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (423, 9, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (424, 9, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (425, 9, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (426, 9, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (427, 9, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (428, 9, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (429, 9, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (430, 9, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (431, 9, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (432, 9, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (433, 9, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (434, 9, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (435, 9, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (436, 9, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (437, 9, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (438, 9, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (439, 9, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (440, 9, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (441, 9, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (442, 9, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (443, 9, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (444, 9, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (445, 9, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (446, 9, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (447, 9, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (448, 9, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (449, 9, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (450, 9, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (451, 10, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (452, 10, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (453, 10, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (454, 10, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (455, 10, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (456, 10, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (457, 10, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (458, 10, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (459, 10, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (460, 10, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (461, 10, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (462, 10, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (463, 10, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (464, 10, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (465, 10, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (466, 10, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (467, 10, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (468, 10, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (469, 10, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (470, 10, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (471, 10, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (472, 10, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (473, 10, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (474, 10, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (475, 10, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (476, 10, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (477, 10, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (478, 10, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (479, 10, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (480, 10, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (481, 10, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (482, 10, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (483, 10, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (484, 10, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (485, 10, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (486, 10, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (487, 10, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (488, 10, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (489, 10, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (490, 10, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (491, 10, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (492, 10, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (493, 10, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (494, 10, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (495, 10, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (496, 10, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (497, 10, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (498, 10, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (499, 10, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (500, 10, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (501, 11, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (502, 11, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (503, 11, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (504, 11, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (505, 11, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (506, 11, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (507, 11, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (508, 11, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (509, 11, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (510, 11, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (511, 11, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (512, 11, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (513, 11, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (514, 11, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (515, 11, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (516, 11, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (517, 11, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (518, 11, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (519, 11, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (520, 11, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (521, 11, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (522, 11, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (523, 11, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (524, 11, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (525, 11, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (526, 11, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (527, 11, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (528, 11, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (529, 11, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (530, 11, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (531, 11, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (532, 11, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (533, 11, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (534, 11, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (535, 11, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (536, 11, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (537, 11, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (538, 11, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (539, 11, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (540, 11, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (541, 11, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (542, 11, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (543, 11, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (544, 11, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (545, 11, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (546, 11, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (547, 11, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (548, 11, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (549, 11, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (550, 11, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (551, 12, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (552, 12, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (553, 12, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (554, 12, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (555, 12, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (556, 12, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (557, 12, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (558, 12, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (559, 12, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (560, 12, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (561, 12, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (562, 12, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (563, 12, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (564, 12, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (565, 12, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (566, 12, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (567, 12, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (568, 12, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (569, 12, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (570, 12, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (571, 12, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (572, 12, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (573, 12, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (574, 12, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (575, 12, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (576, 12, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (577, 12, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (578, 12, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (579, 12, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (580, 12, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (581, 12, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (582, 12, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (583, 12, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (584, 12, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (585, 12, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (586, 12, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (587, 12, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (588, 12, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (589, 12, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (590, 12, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (591, 12, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (592, 12, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (593, 12, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (594, 12, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (595, 12, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (596, 12, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (597, 12, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (598, 12, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (599, 12, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (600, 12, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (601, 13, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (602, 13, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (603, 13, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (604, 13, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (605, 13, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (606, 13, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (607, 13, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (608, 13, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (609, 13, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (610, 13, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (611, 13, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (612, 13, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (613, 13, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (614, 13, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (615, 13, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (616, 13, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (617, 13, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (618, 13, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (619, 13, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (620, 13, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (621, 13, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (622, 13, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (623, 13, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (624, 13, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (625, 13, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (626, 13, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (627, 13, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (628, 13, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (629, 13, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (630, 13, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (631, 13, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (632, 13, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (633, 13, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (634, 13, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (635, 13, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (636, 13, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (637, 13, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (638, 13, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (639, 13, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (640, 13, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (641, 13, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (642, 13, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (643, 13, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (644, 13, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (645, 13, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (646, 13, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (647, 13, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (648, 13, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (649, 13, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (650, 13, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (651, 14, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (652, 14, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (653, 14, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (654, 14, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (655, 14, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (656, 14, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (657, 14, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (658, 14, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (659, 14, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (660, 14, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (661, 14, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (662, 14, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (663, 14, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (664, 14, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (665, 14, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (666, 14, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (667, 14, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (668, 14, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (669, 14, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (670, 14, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (671, 14, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (672, 14, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (673, 14, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (674, 14, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (675, 14, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (676, 14, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (677, 14, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (678, 14, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (679, 14, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (680, 14, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (681, 14, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (682, 14, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (683, 14, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (684, 14, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (685, 14, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (686, 14, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (687, 14, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (688, 14, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (689, 14, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (690, 14, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (691, 14, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (692, 14, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (693, 14, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (694, 14, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (695, 14, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (696, 14, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (697, 14, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (698, 14, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (699, 14, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (700, 14, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (701, 15, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (702, 15, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (703, 15, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (704, 15, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (705, 15, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (706, 15, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (707, 15, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (708, 15, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (709, 15, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (710, 15, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (711, 15, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (712, 15, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (713, 15, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (714, 15, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (715, 15, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (716, 15, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (717, 15, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (718, 15, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (719, 15, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (720, 15, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (721, 15, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (722, 15, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (723, 15, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (724, 15, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (725, 15, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (726, 15, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (727, 15, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (728, 15, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (729, 15, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (730, 15, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (731, 15, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (732, 15, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (733, 15, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (734, 15, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (735, 15, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (736, 15, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (737, 15, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (738, 15, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (739, 15, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (740, 15, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (741, 15, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (742, 15, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (743, 15, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (744, 15, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (745, 15, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (746, 15, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (747, 15, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (748, 15, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (749, 15, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (750, 15, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (751, 16, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (752, 16, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (753, 16, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (754, 16, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (755, 16, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (756, 16, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (757, 16, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (758, 16, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (759, 16, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (760, 16, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (761, 16, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (762, 16, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (763, 16, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (764, 16, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (765, 16, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (766, 16, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (767, 16, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (768, 16, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (769, 16, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (770, 16, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (771, 16, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (772, 16, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (773, 16, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (774, 16, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (775, 16, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (776, 16, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (777, 16, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (778, 16, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (779, 16, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (780, 16, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (781, 16, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (782, 16, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (783, 16, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (784, 16, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (785, 16, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (786, 16, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (787, 16, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (788, 16, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (789, 16, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (790, 16, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (791, 16, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (792, 16, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (793, 16, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (794, 16, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (795, 16, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (796, 16, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (797, 16, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (798, 16, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (799, 16, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (800, 16, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (801, 17, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (802, 17, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (803, 17, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (804, 17, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (805, 17, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (806, 17, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (807, 17, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (808, 17, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (809, 17, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (810, 17, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (811, 17, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (812, 17, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (813, 17, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (814, 17, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (815, 17, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (816, 17, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (817, 17, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (818, 17, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (819, 17, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (820, 17, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (821, 17, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (822, 17, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (823, 17, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (824, 17, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (825, 17, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (826, 17, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (827, 17, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (828, 17, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (829, 17, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (830, 17, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (831, 17, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (832, 17, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (833, 17, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (834, 17, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (835, 17, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (836, 17, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (837, 17, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (838, 17, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (839, 17, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (840, 17, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (841, 17, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (842, 17, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (843, 17, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (844, 17, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (845, 17, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (846, 17, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (847, 17, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (848, 17, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (849, 17, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (850, 17, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (851, 18, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (852, 18, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (853, 18, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (854, 18, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (855, 18, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (856, 18, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (857, 18, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (858, 18, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (859, 18, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (860, 18, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (861, 18, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (862, 18, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (863, 18, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (864, 18, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (865, 18, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (866, 18, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (867, 18, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (868, 18, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (869, 18, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (870, 18, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (871, 18, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (872, 18, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (873, 18, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (874, 18, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (875, 18, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (876, 18, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (877, 18, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (878, 18, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (879, 18, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (880, 18, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (881, 18, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (882, 18, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (883, 18, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (884, 18, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (885, 18, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (886, 18, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (887, 18, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (888, 18, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (889, 18, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (890, 18, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (891, 18, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (892, 18, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (893, 18, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (894, 18, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (895, 18, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (896, 18, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (897, 18, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (898, 18, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (899, 18, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (900, 18, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (901, 19, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (902, 19, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (903, 19, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (904, 19, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (905, 19, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (906, 19, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (907, 19, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (908, 19, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (909, 19, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (910, 19, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (911, 19, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (912, 19, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (913, 19, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (914, 19, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (915, 19, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (916, 19, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (917, 19, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (918, 19, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (919, 19, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (920, 19, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (921, 19, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (922, 19, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (923, 19, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (924, 19, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (925, 19, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (926, 19, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (927, 19, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (928, 19, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (929, 19, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (930, 19, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (931, 19, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (932, 19, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (933, 19, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (934, 19, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (935, 19, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (936, 19, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (937, 19, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (938, 19, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (939, 19, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (940, 19, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (941, 19, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (942, 19, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (943, 19, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (944, 19, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (945, 19, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (946, 19, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (947, 19, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (948, 19, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (949, 19, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (950, 19, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (951, 20, '001', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (952, 20, '002', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (953, 20, '003', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (954, 20, '004', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (955, 20, '005', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 1);
INSERT INTO `seat` VALUES (956, 20, '006', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 2);
INSERT INTO `seat` VALUES (957, 20, '007', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 3);
INSERT INTO `seat` VALUES (958, 20, '008', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 2, 4);
INSERT INTO `seat` VALUES (959, 20, '009', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 1);
INSERT INTO `seat` VALUES (960, 20, '010', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 2);
INSERT INTO `seat` VALUES (961, 20, '011', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 3);
INSERT INTO `seat` VALUES (962, 20, '012', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 3, 4);
INSERT INTO `seat` VALUES (963, 20, '013', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 1);
INSERT INTO `seat` VALUES (964, 20, '014', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 2);
INSERT INTO `seat` VALUES (965, 20, '015', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 3);
INSERT INTO `seat` VALUES (966, 20, '016', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 4, 4);
INSERT INTO `seat` VALUES (967, 20, '017', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 1);
INSERT INTO `seat` VALUES (968, 20, '018', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 2);
INSERT INTO `seat` VALUES (969, 20, '019', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 3);
INSERT INTO `seat` VALUES (970, 20, '020', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 5, 4);
INSERT INTO `seat` VALUES (971, 20, '021', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 1);
INSERT INTO `seat` VALUES (972, 20, '022', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 2);
INSERT INTO `seat` VALUES (973, 20, '023', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 3);
INSERT INTO `seat` VALUES (974, 20, '024', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 6, 4);
INSERT INTO `seat` VALUES (975, 20, '025', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 1);
INSERT INTO `seat` VALUES (976, 20, '026', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 2);
INSERT INTO `seat` VALUES (977, 20, '027', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 3);
INSERT INTO `seat` VALUES (978, 20, '028', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 7, 4);
INSERT INTO `seat` VALUES (979, 20, '029', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 1);
INSERT INTO `seat` VALUES (980, 20, '030', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 2);
INSERT INTO `seat` VALUES (981, 20, '031', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 3);
INSERT INTO `seat` VALUES (982, 20, '032', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 8, 4);
INSERT INTO `seat` VALUES (983, 20, '033', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 1);
INSERT INTO `seat` VALUES (984, 20, '034', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 2);
INSERT INTO `seat` VALUES (985, 20, '035', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 3);
INSERT INTO `seat` VALUES (986, 20, '036', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 9, 4);
INSERT INTO `seat` VALUES (987, 20, '037', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 1);
INSERT INTO `seat` VALUES (988, 20, '038', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 2);
INSERT INTO `seat` VALUES (989, 20, '039', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 3);
INSERT INTO `seat` VALUES (990, 20, '040', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 10, 4);
INSERT INTO `seat` VALUES (991, 20, '041', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 1);
INSERT INTO `seat` VALUES (992, 20, '042', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 2);
INSERT INTO `seat` VALUES (993, 20, '043', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 3);
INSERT INTO `seat` VALUES (994, 20, '044', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 11, 4);
INSERT INTO `seat` VALUES (995, 20, '045', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 1);
INSERT INTO `seat` VALUES (996, 20, '046', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 2);
INSERT INTO `seat` VALUES (997, 20, '047', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 3);
INSERT INTO `seat` VALUES (998, 20, '048', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 12, 4);
INSERT INTO `seat` VALUES (999, 20, '049', '普通座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 1);
INSERT INTO `seat` VALUES (1000, 20, '050', '电脑座位', '正常', '2026-03-16 14:25:22', '2026-05-17 13:53:30', 13, 2);
INSERT INTO `seat` VALUES (1001, 1, '056', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 14, 4);
INSERT INTO `seat` VALUES (1002, 1, '055', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 14, 3);
INSERT INTO `seat` VALUES (1003, 1, '052', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 13, 4);
INSERT INTO `seat` VALUES (1004, 1, '051', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 13, 3);
INSERT INTO `seat` VALUES (1005, 1, '054', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 14, 2);
INSERT INTO `seat` VALUES (1006, 1, '053', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 14, 1);
INSERT INTO `seat` VALUES (1007, 1, '061', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 14:14:39', 15, 1);
INSERT INTO `seat` VALUES (1008, 1, '063', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 14:14:39', 15, 2);
INSERT INTO `seat` VALUES (1009, 1, '059', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 15, 3);
INSERT INTO `seat` VALUES (1010, 1, '060', '静音座位', '正常', '2026-03-16 21:38:35', '2026-05-17 13:53:30', 15, 4);
INSERT INTO `seat` VALUES (1011, 21, '004', '普通座位', '正常', '2026-05-16 01:01:33', '2026-05-17 13:53:30', 1, 4);
INSERT INTO `seat` VALUES (1012, 21, '003', '普通座位', '正常', '2026-05-16 01:01:33', '2026-05-17 13:53:30', 1, 3);
INSERT INTO `seat` VALUES (1013, 21, '002', '普通座位', '正常', '2026-05-16 01:01:33', '2026-05-17 13:53:30', 1, 2);
INSERT INTO `seat` VALUES (1014, 21, '001', '普通座位', '正常', '2026-05-16 01:01:33', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (1015, 22, '1', '普通座位', '维修', '2026-05-16 15:19:55', '2026-05-17 13:53:30', 1, 1);
INSERT INTO `seat` VALUES (1016, 1, '069', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 15, 1);
INSERT INTO `seat` VALUES (1017, 1, '068', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 14, 4);
INSERT INTO `seat` VALUES (1018, 1, '067', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 14, 3);
INSERT INTO `seat` VALUES (1019, 1, '065', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 14, 1);
INSERT INTO `seat` VALUES (1020, 1, '066', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 14, 2);
INSERT INTO `seat` VALUES (1021, 1, '062', '普通座位', '正常', '2026-05-17 13:46:36', '2026-05-17 13:46:36', 1, 5);
INSERT INTO `seat` VALUES (1022, 1, '072', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 15, 4);
INSERT INTO `seat` VALUES (1023, 1, '073', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 16, 1);
INSERT INTO `seat` VALUES (1024, 1, '064', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 14:14:39', 15, 2);
INSERT INTO `seat` VALUES (1025, 1, '071', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 15, 3);
INSERT INTO `seat` VALUES (1026, 1, '076', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 16, 4);
INSERT INTO `seat` VALUES (1027, 1, '077', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 17, 1);
INSERT INTO `seat` VALUES (1028, 1, '070', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 14:14:39', 16, 2);
INSERT INTO `seat` VALUES (1029, 1, '075', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 16, 3);
INSERT INTO `seat` VALUES (1030, 1, '079', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 17, 3);
INSERT INTO `seat` VALUES (1031, 1, '074', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 14:14:39', 17, 2);
INSERT INTO `seat` VALUES (1032, 1, '080', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 17, 4);
INSERT INTO `seat` VALUES (1033, 1, '081', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 18, 1);
INSERT INTO `seat` VALUES (1034, 1, '078', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 14:14:40', 18, 2);
INSERT INTO `seat` VALUES (1035, 1, '083', '普通座位', '正常', '2026-05-17 13:46:37', '2026-05-17 13:46:37', 18, 3);
INSERT INTO `seat` VALUES (1036, 1, '085', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 19, 1);
INSERT INTO `seat` VALUES (1037, 1, '082', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 14:14:40', 19, 2);
INSERT INTO `seat` VALUES (1038, 1, '087', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 19, 3);
INSERT INTO `seat` VALUES (1039, 1, '088', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 19, 4);
INSERT INTO `seat` VALUES (1040, 1, '084', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 18, 4);
INSERT INTO `seat` VALUES (1041, 1, '089', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 20, 1);
INSERT INTO `seat` VALUES (1042, 1, '091', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 20, 3);
INSERT INTO `seat` VALUES (1043, 1, '086', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 14:14:40', 20, 2);
INSERT INTO `seat` VALUES (1044, 1, '093', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 21, 1);
INSERT INTO `seat` VALUES (1045, 1, '092', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 20, 4);
INSERT INTO `seat` VALUES (1046, 1, '096', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 21, 4);
INSERT INTO `seat` VALUES (1047, 1, '095', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 21, 3);
INSERT INTO `seat` VALUES (1048, 1, '099', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 22, 3);
INSERT INTO `seat` VALUES (1049, 1, '097', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 13:46:38', 22, 1);
INSERT INTO `seat` VALUES (1050, 1, '094', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 14:14:41', 22, 2);
INSERT INTO `seat` VALUES (1051, 1, '090', '普通座位', '正常', '2026-05-17 13:46:38', '2026-05-17 14:14:40', 21, 2);
INSERT INTO `seat` VALUES (1052, 1, '103', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 23, 3);
INSERT INTO `seat` VALUES (1053, 1, '098', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 14:14:41', 23, 2);
INSERT INTO `seat` VALUES (1054, 1, '101', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 23, 1);
INSERT INTO `seat` VALUES (1055, 1, '100', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 22, 4);
INSERT INTO `seat` VALUES (1056, 1, '104', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 23, 4);
INSERT INTO `seat` VALUES (1057, 1, '107', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 24, 3);
INSERT INTO `seat` VALUES (1058, 1, '108', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 24, 4);
INSERT INTO `seat` VALUES (1059, 1, '109', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 25, 1);
INSERT INTO `seat` VALUES (1060, 1, '105', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 24, 1);
INSERT INTO `seat` VALUES (1061, 1, '102', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 14:14:41', 24, 2);
INSERT INTO `seat` VALUES (1062, 1, '113', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 26, 1);
INSERT INTO `seat` VALUES (1063, 1, '112', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 25, 4);
INSERT INTO `seat` VALUES (1064, 1, '111', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 13:46:39', 25, 3);
INSERT INTO `seat` VALUES (1065, 1, '106', '普通座位', '正常', '2026-05-17 13:46:39', '2026-05-17 14:14:41', 25, 2);
INSERT INTO `seat` VALUES (1066, 1, '110', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 14:14:41', 26, 2);
INSERT INTO `seat` VALUES (1067, 1, '114', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 14:14:42', 27, 2);
INSERT INTO `seat` VALUES (1068, 1, '119', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 27, 3);
INSERT INTO `seat` VALUES (1069, 1, '117', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 27, 1);
INSERT INTO `seat` VALUES (1070, 1, '116', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 26, 4);
INSERT INTO `seat` VALUES (1071, 1, '115', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 26, 3);
INSERT INTO `seat` VALUES (1072, 1, '118', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 14:14:42', 28, 2);
INSERT INTO `seat` VALUES (1073, 1, '123', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 28, 3);
INSERT INTO `seat` VALUES (1074, 1, '120', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 27, 4);
INSERT INTO `seat` VALUES (1075, 1, '121', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 28, 1);
INSERT INTO `seat` VALUES (1076, 1, '127', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 29, 3);
INSERT INTO `seat` VALUES (1077, 1, '124', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 28, 4);
INSERT INTO `seat` VALUES (1078, 1, '129', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 30, 1);
INSERT INTO `seat` VALUES (1079, 1, '122', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 14:14:42', 29, 2);
INSERT INTO `seat` VALUES (1080, 1, '128', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 29, 4);
INSERT INTO `seat` VALUES (1081, 1, '125', '普通座位', '正常', '2026-05-17 13:46:40', '2026-05-17 13:46:40', 29, 1);
INSERT INTO `seat` VALUES (1082, 1, '131', '普通座位', '正常', '2026-05-17 13:46:41', '2026-05-17 13:46:41', 30, 3);
INSERT INTO `seat` VALUES (1083, 1, '126', '普通座位', '正常', '2026-05-17 13:46:41', '2026-05-17 14:14:42', 30, 2);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'daily_order_sequence', '21', '每日预约流水号序列', '2026-03-16 14:25:22', '2026-03-16 14:25:22');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(MD5加密)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '学生' COMMENT '用户类型: 学生, 管理员',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '状态: 正常, 禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', NULL, NULL, '图书馆管理员', '正常', '2026-03-16 14:25:22', '2026-05-05 16:23:26');
INSERT INTO `user` VALUES (2, 'student001', '22cf837d2060f69e9ee9f7d98bc8e2cb', '张三', NULL, NULL, '学生', '正常', '2026-03-16 14:25:22', '2026-03-16 14:25:22');
INSERT INTO `user` VALUES (3, 'student002', '8f8ab797bad56ccdaf4bc1c6ce194093', '李四', NULL, NULL, '学生', '正常', '2026-03-16 14:25:22', '2026-03-16 14:25:22');
INSERT INTO `user` VALUES (4, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13437510723', '2789216224@qq.com', '学生', '正常', '2026-03-16 17:49:23', '2026-03-16 17:49:23');
INSERT INTO `user` VALUES (6, 'system_admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@library.com', '系统管理员', '正常', '2026-05-05 15:17:29', '2026-05-05 15:17:29');
INSERT INTO `user` VALUES (7, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李斯', '15346781976', '15346781976@qq.com', '学生', '正常', '2026-05-05 22:02:10', '2026-05-05 22:02:10');
INSERT INTO `user` VALUES (8, 'yyy', 'e10adc3949ba59abbe56e057f20f883e', 'yyy', '13457216437', '13457216437@qq.com', '学生', '正常', '2026-05-14 01:12:57', '2026-05-14 01:12:57');
INSERT INTO `user` VALUES (9, 'lp', 'e10adc3949ba59abbe56e057f20f883e', '李飘', '13497815701', '13475497215@163.com', '学生', '正常', '2026-05-16 13:14:00', '2026-05-16 13:14:00');

-- ----------------------------
-- Table structure for user_points
-- ----------------------------
DROP TABLE IF EXISTS `user_points`;
CREATE TABLE `user_points`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学工号',
  `total_points` int NOT NULL DEFAULT 60 COMMENT '总积分（可超出120）',
  `current_points` int NOT NULL DEFAULT 60 COMMENT '当前有效积分（最高按100计算权益）',
  `credit_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '一般' COMMENT '信用等级：极好/良好/一般/较差/暂停服务',
  `max_reservation_hours` int NOT NULL DEFAULT 2 COMMENT '最大预约时长（小时）',
  `max_reservation_count` int NOT NULL DEFAULT 2 COMMENT '每日最大预约次数',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_credit_level`(`credit_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户积分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_points
-- ----------------------------
INSERT INTO `user_points` VALUES (1, 3, 'student002', 80, 80, '良好', 4, 3, '2026-05-05 22:00:53', '2026-05-05 16:46:45', '2026-05-16 13:11:32');
INSERT INTO `user_points` VALUES (2, 2, 'student001', 86, 70, '一般', 2, 2, '2026-05-17 21:12:54', '2026-05-05 16:47:36', '2026-05-17 22:12:27');
INSERT INTO `user_points` VALUES (3, 7, 'lisi', 80, 80, '良好', 2, 2, '2026-05-06 16:43:22', '2026-05-05 22:02:10', '2026-05-16 13:11:32');
INSERT INTO `user_points` VALUES (4, 4, 'wangwu', 80, 80, '良好', 1, 1, '2026-05-13 09:07:08', '2026-05-08 20:27:39', '2026-05-16 13:11:32');
INSERT INTO `user_points` VALUES (5, 8, 'yyy', 68, 63, '一般', 2, 2, '2026-05-18 14:20:00', '2026-05-14 01:12:58', '2026-05-20 15:30:25');
INSERT INTO `user_points` VALUES (6, 9, 'lp', 83, 83, '良好', 4, 3, '2026-05-18 14:45:44', '2026-05-16 13:14:01', '2026-05-20 15:17:48');

-- ----------------------------
-- Table structure for violation_record
-- ----------------------------
DROP TABLE IF EXISTS `violation_record`;
CREATE TABLE `violation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '违规用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '违规用户姓名（冗余）',
  `student_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号（冗余）',
  `violation_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '违规类型：爽约/暂离超时/未签退离馆/恶意占座',
  `reservation_id` bigint NULL DEFAULT NULL COMMENT '关联预约ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联预约流水号',
  `rule_id` bigint NULL DEFAULT NULL COMMENT '触发的规则ID',
  `points_deducted` int NOT NULL DEFAULT 0 COMMENT '实际扣分',
  `ban_days` int NOT NULL DEFAULT 0 COMMENT '实际封禁天数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待处理' COMMENT '状态：待处理/已处理/已豁免',
  `handle_reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注/豁免理由',
  `handled_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handled_by_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理人姓名（冗余）',
  `violation_time` datetime NOT NULL COMMENT '违规发生时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_violation_type`(`violation_type` ASC) USING BTREE,
  INDEX `idx_violation_time`(`violation_time` ASC) USING BTREE,
  INDEX `idx_reservation_id`(`reservation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '违规记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of violation_record
-- ----------------------------
INSERT INTO `violation_record` VALUES (1, 8, 'yyy', 'yyy', '爽约', 18, '20260518-0018', NULL, -6, 0, '待处理', NULL, NULL, NULL, '2026-05-18 13:50:00', NULL, NULL, NULL);
INSERT INTO `violation_record` VALUES (2, 8, 'yyy', 'yyy', '爽约', 19, '20260518-0019', NULL, -6, 0, '待处理', NULL, NULL, NULL, '2026-05-18 14:20:00', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
