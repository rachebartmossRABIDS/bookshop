-- Bookshop P5 migration: database-backed request idempotency.
-- token_version is introduced by bookshop-p3-migration.sql and reused by P5 JWT revocation.

CREATE TABLE IF NOT EXISTS `order_request` (
  `request_id` VARCHAR(64) NOT NULL,
  `uid` INT NOT NULL,
  `result_json` TEXT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`request_id`,`uid`),
  CONSTRAINT `fk_order_request_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='order request idempotency';
