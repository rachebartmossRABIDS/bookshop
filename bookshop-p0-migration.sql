-- ============================================================
-- P0 迁移脚本（在已初始化的 bookshop 库上执行一次）
-- 内容：
--   1. book.inventory / book.volume 由 varchar(30) 改为 INT
--   2. order 表新增 amount 金额快照字段，并按 现价×数量 回填历史订单
-- 全新安装请直接使用更新后的 bookshop.sql，无需执行本脚本。
-- ============================================================
USE bookshop;

-- 1. 库存/销量 INT 化（先清洗非法值，避免严格模式报错）
UPDATE book SET inventory = '0' WHERE inventory IS NULL OR inventory NOT REGEXP '^[0-9]+$';
UPDATE book SET volume = '0' WHERE volume IS NULL OR volume NOT REGEXP '^[0-9]+$';
ALTER TABLE book
  MODIFY COLUMN inventory int NOT NULL DEFAULT 0 COMMENT '库存',
  MODIFY COLUMN volume int NOT NULL DEFAULT 0 COMMENT '销量';

-- 2. 订单金额快照
ALTER TABLE `order`
  ADD COLUMN amount decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单金额（下单时单价×数量快照）' AFTER num;
UPDATE `order` o JOIN book b ON o.bid = b.bid
SET o.amount = b.price * o.num
WHERE o.amount = 0;

-- 订单状态语义（state 字段注释更新）：
-- 0待付款 1已付款 2已发货 3已收货(终态) 4交易成功(终态,历史数据) 5已取消(终态)
ALTER TABLE `order`
  MODIFY COLUMN state varchar(20) DEFAULT NULL COMMENT '订单状态：0待付款 1已付款 2已发货 3已收货 4交易成功 5已取消';