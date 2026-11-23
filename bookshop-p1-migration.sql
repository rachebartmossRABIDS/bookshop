-- P1 迁移：收藏功能
-- 在已应用 P0 迁移的 bookshop 库上执行（可重复执行）
USE bookshop;

CREATE TABLE IF NOT EXISTS `favorite` (
  `fid` int NOT NULL AUTO_INCREMENT COMMENT '收藏编号',
  `uid` int NOT NULL COMMENT '用户编号',
  `bid` int NOT NULL COMMENT '书籍编号',
  `time` datetime DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`fid`),
  UNIQUE KEY `uk_uid_bid` (`uid`,`bid`),
  KEY `fk_favorite_book` (`bid`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏';