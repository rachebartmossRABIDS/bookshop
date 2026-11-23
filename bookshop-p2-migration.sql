-- P2 迁移：购物车
-- 在已应用 P0/P1 迁移的 bookshop 库上执行（可重复执行）
USE bookshop;

CREATE TABLE IF NOT EXISTS `cart` (
  `cid` int NOT NULL AUTO_INCREMENT COMMENT '购物车编号',
  `uid` int NOT NULL COMMENT '用户编号',
  `bid` int NOT NULL COMMENT '书籍编号',
  `num` int NOT NULL DEFAULT 1 COMMENT '数量',
  `time` datetime DEFAULT NULL COMMENT '加入时间',
  PRIMARY KEY (`cid`),
  UNIQUE KEY `uk_cart_uid_bid` (`uid`,`bid`),
  KEY `fk_cart_book` (`bid`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `fk_cart_book` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车';