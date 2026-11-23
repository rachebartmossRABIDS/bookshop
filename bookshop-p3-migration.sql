-- Bookshop P3 migration: payment/address/order logistics/comment enhancement/search/reset/admin logs/notifications.
-- Target: MySQL 8.x, apply after bookshop-p2-migration.sql.

ALTER TABLE `user`
    ADD COLUMN `token_version` INT NOT NULL DEFAULT 0 COMMENT 'JWT token version' AFTER `state`;

ALTER TABLE `order`
    ADD COLUMN `ship_time` DATETIME NULL COMMENT 'ship time' AFTER `tel`,
    ADD COLUMN `logistics_company` VARCHAR(100) NULL COMMENT 'logistics company' AFTER `ship_time`,
    ADD COLUMN `logistics_no` VARCHAR(100) NULL COMMENT 'logistics number' AFTER `logistics_company`;

ALTER TABLE `comment`
    ADD COLUMN `score` INT NOT NULL DEFAULT 5 COMMENT 'rating 1-5' AFTER `oid`,
    ADD COLUMN `reply` TEXT NULL COMMENT 'merchant reply' AFTER `score`,
    ADD COLUMN `reply_time` DATETIME NULL COMMENT 'reply time' AFTER `reply`;

CREATE TABLE IF NOT EXISTS `payment` (
  `pid` INT NOT NULL AUTO_INCREMENT,
  `oid` INT NOT NULL,
  `channel` VARCHAR(32) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `trade_no` VARCHAR(100) NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `uk_payment_oid` (`oid`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`oid`) REFERENCES `order` (`oid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='payment records';

CREATE TABLE IF NOT EXISTS `address` (
  `aid` INT NOT NULL AUTO_INCREMENT,
  `uid` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `tel` VARCHAR(32) NOT NULL,
  `addr` VARCHAR(500) NOT NULL,
  `is_default` TINYINT NOT NULL DEFAULT 0,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`aid`),
  KEY `idx_address_uid` (`uid`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='shipping addresses';

CREATE TABLE IF NOT EXISTS `comment_like` (
  `lid` INT NOT NULL AUTO_INCREMENT,
  `uid` INT NOT NULL,
  `cid` INT NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`lid`),
  UNIQUE KEY `uk_comment_like_uid_cid` (`uid`,`cid`),
  KEY `idx_comment_like_cid` (`cid`),
  CONSTRAINT `fk_comment_like_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `fk_comment_like_comment` FOREIGN KEY (`cid`) REFERENCES `comment` (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='comment likes';

CREATE TABLE IF NOT EXISTS `search_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(255) NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_search_log_time` (`time`),
  KEY `idx_search_log_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='search history';

CREATE TABLE IF NOT EXISTS `reset_code` (
  `rid` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `code` VARCHAR(16) NOT NULL,
  `expire_time` DATETIME NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`rid`),
  KEY `idx_reset_code_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='password reset codes';

CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uid` INT NULL,
  `action` VARCHAR(100) NOT NULL,
  `detail` VARCHAR(1000) NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='admin operation log';

CREATE TABLE IF NOT EXISTS `notification` (
  `nid` INT NOT NULL AUTO_INCREMENT,
  `uid` INT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` VARCHAR(1000) NOT NULL,
  `is_read` TINYINT NOT NULL DEFAULT 0,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`nid`),
  KEY `idx_notification_uid_time` (`uid`,`time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='user notifications';
