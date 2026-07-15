-- H2 兼容的建表语句（MODE=MySQL）

DROP TABLE IF EXISTS violation_record;
DROP TABLE IF EXISTS user_points;
DROP TABLE IF EXISTS punishment_rule;
DROP TABLE IF EXISTS points_record;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS complaint;
DROP TABLE IF EXISTS appeal;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS reservation_rule;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS library;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS system_config;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
  id BIGINT AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(32) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  phone VARCHAR(11) NULL,
  email VARCHAR(100) NULL,
  user_type VARCHAR(20) NOT NULL DEFAULT '学生',
  status VARCHAR(10) NOT NULL DEFAULT '正常',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
);

CREATE TABLE library (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(200) NULL,
  description TEXT NULL,
  status VARCHAR(10) NOT NULL DEFAULT '正常',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE seat (
  id BIGINT AUTO_INCREMENT,
  library_id BIGINT NOT NULL,
  seat_number VARCHAR(20) NOT NULL,
  seat_type VARCHAR(20) NULL DEFAULT '普通座位',
  status VARCHAR(10) NOT NULL DEFAULT '正常',
  row_num INT NULL DEFAULT 0,
  col_num INT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_library_seat (library_id, seat_number),
  FOREIGN KEY (library_id) REFERENCES library(id)
);

CREATE TABLE reservation_rule (
  id BIGINT AUTO_INCREMENT,
  rule_key VARCHAR(50) NOT NULL,
  rule_name VARCHAR(100) NOT NULL,
  rule_value VARCHAR(500) NOT NULL,
  description VARCHAR(500) NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_rule_key (rule_key)
);

CREATE TABLE reservation (
  id BIGINT AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL,
  user_id BIGINT NOT NULL,
  library_id BIGINT NOT NULL,
  seat_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT '已预约',
  qr_code TEXT NULL,
  check_in_time TIMESTAMP NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  FOREIGN KEY (library_id) REFERENCES library(id),
  FOREIGN KEY (seat_id) REFERENCES seat(id),
  FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE user_points (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  student_no VARCHAR(50) NULL,
  total_points INT NOT NULL DEFAULT 60,
  current_points INT NOT NULL DEFAULT 60,
  credit_level VARCHAR(20) NOT NULL DEFAULT '一般',
  max_reservation_hours INT NOT NULL DEFAULT 2,
  max_reservation_count INT NOT NULL DEFAULT 2,
  last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE punishment_rule (
  id BIGINT AUTO_INCREMENT,
  violation_type VARCHAR(30) NOT NULL,
  occurrence_count INT NOT NULL DEFAULT 1,
  points_deducted INT NOT NULL DEFAULT 0,
  ban_days INT NOT NULL DEFAULT 0,
  description VARCHAR(500) NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE system_config (
  id BIGINT AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL,
  config_value VARCHAR(500) NOT NULL,
  description VARCHAR(500) NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_config_key (config_key)
);

CREATE TABLE announcement (
  id BIGINT AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(50) NULL DEFAULT '普通公告',
  priority INT NULL DEFAULT 0,
  status VARCHAR(20) NULL DEFAULT '发布中',
  publish_time TIMESTAMP NULL,
  expire_time TIMESTAMP NULL,
  create_by VARCHAR(50) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE banner (
  id BIGINT AUTO_INCREMENT,
  title VARCHAR(200) NULL,
  image_url VARCHAR(500) NOT NULL,
  link_url VARCHAR(500) NULL,
  sort_order INT NULL DEFAULT 0,
  status VARCHAR(20) NULL DEFAULT '启用',
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE notification (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT NULL,
  type VARCHAR(50) NULL DEFAULT '系统通知',
  is_read TINYINT NULL DEFAULT 0,
  link VARCHAR(500) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE feedback (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50) NULL,
  student_no VARCHAR(30) NULL,
  type VARCHAR(20) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  image_url VARCHAR(500) NULL,
  status VARCHAR(20) NOT NULL DEFAULT '待处理',
  handler_id BIGINT NULL,
  handler_name VARCHAR(50) NULL,
  reply TEXT NULL,
  handle_result VARCHAR(20) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  handle_time TIMESTAMP NULL,
  update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE appeal (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  points_record_id BIGINT NOT NULL,
  reason VARCHAR(500) NOT NULL,
  image_url VARCHAR(255) NULL,
  status VARCHAR(20) NULL DEFAULT '待审核',
  handler_id BIGINT NULL,
  reply VARCHAR(500) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  handle_time TIMESTAMP NULL,
  PRIMARY KEY (id)
);

CREATE TABLE complaint (
  id BIGINT AUTO_INCREMENT,
  reporter_id BIGINT NOT NULL,
  occupant_id BIGINT NULL,
  seat_id BIGINT NOT NULL,
  library_id BIGINT NOT NULL,
  reporter_reservation_id BIGINT NOT NULL,
  occupant_reservation_id BIGINT NULL,
  description VARCHAR(500) NULL,
  status VARCHAR(20) NULL DEFAULT '待处理',
  handler_id BIGINT NULL,
  handler_reply VARCHAR(500) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  handle_time TIMESTAMP NULL,
  PRIMARY KEY (id)
);

CREATE TABLE violation_record (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50) NULL,
  student_no VARCHAR(30) NULL,
  violation_type VARCHAR(30) NOT NULL,
  reservation_id BIGINT NULL,
  order_no VARCHAR(50) NULL,
  rule_id BIGINT NULL,
  points_deducted INT NOT NULL DEFAULT 0,
  ban_days INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT '待处理',
  handle_reply VARCHAR(500) NULL,
  handled_by BIGINT NULL,
  handled_by_name VARCHAR(50) NULL,
  violation_time TIMESTAMP NOT NULL,
  handle_time TIMESTAMP NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE operation_log (
  id BIGINT AUTO_INCREMENT,
  operator_id BIGINT NULL,
  operator_name VARCHAR(50) NULL,
  operation_type VARCHAR(50) NOT NULL,
  module VARCHAR(50) NULL,
  target VARCHAR(200) NULL,
  detail TEXT NULL,
  ip VARCHAR(50) NULL,
  user_agent VARCHAR(500) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE points_record (
  id BIGINT AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  points_before INT NOT NULL,
  points_after INT NOT NULL,
  change_type VARCHAR(50) NOT NULL,
  change_amount INT NOT NULL,
  source VARCHAR(100) NULL,
  related_id BIGINT NULL,
  description VARCHAR(500) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);
