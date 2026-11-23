-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: bookshop
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `bid` int NOT NULL AUTO_INCREMENT COMMENT '图书编号',
  `name` varchar(255) DEFAULT NULL COMMENT '图书名称',
  `time` datetime DEFAULT NULL COMMENT '出版时间',
  `press` varchar(255) DEFAULT NULL COMMENT '出版社',
  `author` varchar(255) DEFAULT NULL COMMENT '作者',
  `isbn` varchar(50) DEFAULT NULL COMMENT 'ISBN编号',
  `image` varchar(500) DEFAULT NULL COMMENT '图书图片',
  `backlist` text COMMENT '图书目录',
  `inventory` int NOT NULL DEFAULT 0 COMMENT '库存',
  `volume` int NOT NULL DEFAULT 0 COMMENT '销量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '图书价格',
  `description` text COMMENT '图书简介',
  `sid` int DEFAULT NULL COMMENT '所属店铺编号',
  `state` int DEFAULT NULL COMMENT '图书状态',
  `kid` int DEFAULT NULL COMMENT '所属分类编号',
  `content` varchar(255) DEFAULT NULL COMMENT '分类内容',
  PRIMARY KEY (`bid`),
  KEY `idx_book_sid` (`sid`),
  KEY `idx_book_kid` (`kid`),
  CONSTRAINT `fk_book_kind` FOREIGN KEY (`kid`) REFERENCES `kind` (`kid`),
  CONSTRAINT `fk_book_shop` FOREIGN KEY (`sid`) REFERENCES `shop` (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (1,'活着','2012-08-01 00:00:00','作家出版社','余华','9787506365437','1.jpg','第一章；第二章；第三章',100,35,35.00,'余华代表作，讲述个人命运与时代变迁。',1,0,1,'文学'),(2,'围城','2017-06-01 00:00:00','人民文学出版社','钱钟书','9787020090006','2.jpg','序；第一章；第二章',80,20,39.00,'描写知识分子生活与社会百态的经典小说。',1,0,1,'文学'),(3,'深入理解计算机系统','2016-11-01 00:00:00','机械工业出版社','Randal E. Bryant','9787111544937','3.jpg','计算机系统漫游；信息的表示和处理；程序的机器级表示',60,28,139.00,'计算机系统基础知识与底层原理经典教材。',2,0,2,'计算机'),(4,'人类简史','2017-02-01 00:00:00','中信出版社','尤瓦尔·赫拉利','9787508672069','4.jpg','人类的认知革命；农业革命；科学革命',90,42,68.00,'从宏观视角讲述人类历史的发展。',3,0,3,'历史'),(5,'时间简史','2010-04-01 00:00:00','湖南科学技术出版社','史蒂芬·霍金','9787535732309','5.jpg','宇宙的图像；空间和时间；黑洞',70,31,45.00,'介绍宇宙学、黑洞和时间等科学知识。',2,0,2,'计算机');
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `cid` int NOT NULL AUTO_INCREMENT COMMENT '评论编号',
  `uid` int DEFAULT NULL COMMENT '用户编号',
  `bid` int DEFAULT NULL COMMENT '图书编号',
  `time` datetime DEFAULT NULL COMMENT '评论时间',
  `content` text COMMENT '评论内容',
  `oid` int DEFAULT NULL COMMENT '订单编号',
  `score` int NOT NULL DEFAULT '5' COMMENT '评分 1-5',
  `reply` text COMMENT '商家回复',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`cid`),
  KEY `idx_comment_uid` (`uid`),
  KEY `idx_comment_bid` (`bid`),
  KEY `idx_comment_oid` (`oid`),
  CONSTRAINT `fk_comment_book` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`),
  CONSTRAINT `fk_comment_order` FOREIGN KEY (`oid`) REFERENCES `order` (`oid`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` (`cid`,`uid`,`bid`,`time`,`content`,`oid`) VALUES (1,1,1,'2026-11-04 10:00:00','内容很有深度，值得阅读。',1),(2,1,3,'2026-11-05 11:00:00','非常适合计算机专业学生学习。',2),(3,1,4,'2026-11-06 12:00:00','从新的角度理解人类历史，很有启发。',3);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind`
--

DROP TABLE IF EXISTS `kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kind` (
  `kid` int NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  PRIMARY KEY (`kid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind`
--

LOCK TABLES `kind` WRITE;
/*!40000 ALTER TABLE `kind` DISABLE KEYS */;
INSERT INTO `kind` VALUES (1,'文学'),(2,'计算机'),(3,'历史'),(4,'科学'),(5,'艺术');
/*!40000 ALTER TABLE `kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `oid` int NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `uid` int DEFAULT NULL COMMENT '用户编号',
  `bid` int DEFAULT NULL COMMENT '图书编号',
  `time` datetime DEFAULT NULL COMMENT '下单时间',
  `num` int DEFAULT NULL COMMENT '购买数量',
  `amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单金额（下单时单价×数量快照）',
  `addr` varchar(500) DEFAULT NULL COMMENT '收件地址',
  `state` varchar(20) DEFAULT NULL COMMENT '订单状态：0待付款 1已付款 2已发货 3已收货 4交易成功 5已取消',
  `name` varchar(100) DEFAULT NULL COMMENT '收件人',
  `tel` varchar(32) DEFAULT NULL COMMENT '收件人电话',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `logistics_company` varchar(100) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(100) DEFAULT NULL COMMENT '物流单号',
  PRIMARY KEY (`oid`),
  KEY `idx_order_uid` (`uid`),
  KEY `idx_order_bid` (`bid`),
  CONSTRAINT `fk_order_book` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` (`oid`,`uid`,`bid`,`time`,`num`,`amount`,`addr`,`state`,`name`,`tel`) VALUES (1,1,1,'2026-11-01 10:00:00',1,35.00,'湖北省武汉市洪山区','1','测试用户','13800000001'),(2,1,3,'2026-11-02 14:30:00',1,139.00,'湖北省武汉市洪山区','2','测试用户','13800000001'),(3,1,4,'2026-11-03 16:00:00',2,136.00,'湖北省武汉市洪山区','4','测试用户','13800000001');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop`
--

DROP TABLE IF EXISTS `shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop` (
  `sid` int NOT NULL AUTO_INCREMENT COMMENT '店铺编号',
  `name` varchar(255) DEFAULT NULL COMMENT '店铺名称',
  `logo` varchar(500) DEFAULT NULL COMMENT '店铺Logo',
  `picture` varchar(500) DEFAULT NULL COMMENT '店铺图片',
  `score` varchar(20) DEFAULT NULL COMMENT '店铺评分',
  `description` text COMMENT '店铺简介',
  `state` varchar(20) DEFAULT NULL COMMENT '店铺状态',
  `kid` int DEFAULT NULL COMMENT '所属分类编号',
  PRIMARY KEY (`sid`),
  KEY `fk_shop_kind` (`kid`),
  CONSTRAINT `fk_shop_kind` FOREIGN KEY (`kid`) REFERENCES `kind` (`kid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop`
--

LOCK TABLES `shop` WRITE;
/*!40000 ALTER TABLE `shop` DISABLE KEYS */;
INSERT INTO `shop` VALUES (1,'经典文学书店','6.jpg','6.jpg','4.9','精选中外文学经典作品','0',1),(2,'科技图书馆','7.jpg','7.jpg','4.8','计算机与科技类图书专营','0',2),(3,'历史文化书屋','8.jpg','8.jpg','4.7','历史文化与人文社科类图书','0',3);
/*!40000 ALTER TABLE `shop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `uid` int NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `tel` varchar(32) DEFAULT NULL COMMENT '用户电话',
  `email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `type` int DEFAULT NULL COMMENT '用户类型',
  `image` varchar(500) DEFAULT NULL COMMENT '用户头像',
  `reputation` varchar(20) DEFAULT NULL COMMENT '信誉等级',
  `state` int DEFAULT NULL COMMENT '用户状态',
  `token_version` int NOT NULL DEFAULT '0' COMMENT 'JWT 令牌版本',
  PRIMARY KEY (`uid`),
  KEY `idx_user_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`uid`,`username`,`password`,`tel`,`email`,`type`,`image`,`reputation`,`state`,`token_version`) VALUES (1,'testuser','e10adc3949ba59abbe56e057f20f883e','13800000001','testuser@example.com',1,'','5',0,0),(2,'admin','e10adc3949ba59abbe56e057f20f883e','13800000002','admin@example.com',2,'','5',0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite` (P1 新增：用户收藏)
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart` (P2 新增：购物车)
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

-- P3 tables
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `pid` int NOT NULL AUTO_INCREMENT,
  `oid` int NOT NULL,
  `channel` varchar(32) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `trade_no` varchar(100) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `uk_payment_oid` (`oid`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`oid`) REFERENCES `order` (`oid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录';

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `aid` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `tel` varchar(32) NOT NULL,
  `addr` varchar(500) NOT NULL,
  `is_default` tinyint NOT NULL DEFAULT '0',
  `time` datetime NOT NULL,
  PRIMARY KEY (`aid`),
  KEY `idx_address_uid` (`uid`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址';

DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like` (
  `lid` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL,
  `cid` int NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`lid`),
  UNIQUE KEY `uk_comment_like_uid_cid` (`uid`,`cid`),
  KEY `idx_comment_like_cid` (`cid`),
  CONSTRAINT `fk_comment_like_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `fk_comment_like_comment` FOREIGN KEY (`cid`) REFERENCES `comment` (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞';

DROP TABLE IF EXISTS `search_log`;
CREATE TABLE `search_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_search_log_time` (`time`),
  KEY `idx_search_log_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搜索日志';

DROP TABLE IF EXISTS `reset_code`;
CREATE TABLE `reset_code` (
  `rid` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `code` varchar(16) NOT NULL,
  `expire_time` datetime NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`rid`),
  KEY `idx_reset_code_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='找回密码验证码';

DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int DEFAULT NULL,
  `action` varchar(100) NOT NULL,
  `detail` varchar(1000) DEFAULT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理操作日志';

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `nid` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `time` datetime NOT NULL,
  PRIMARY KEY (`nid`),
  KEY `idx_notification_uid_time` (`uid`,`time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内通知';

DROP TABLE IF EXISTS `order_request`;
CREATE TABLE `order_request` (
  `request_id` varchar(64) NOT NULL,
  `uid` int NOT NULL,
  `result_json` text,
  `time` datetime NOT NULL,
  PRIMARY KEY (`request_id`,`uid`),
  CONSTRAINT `fk_order_request_user` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='下单幂等记录';

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-15 11:06:01
