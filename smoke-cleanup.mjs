// 冒烟测试残留清理：恢复 bookshop.sql 演示基线（需 npm i mysql2 --no-save）
// 用法：node smoke-cleanup.mjs   可用 DB_HOST/DB_USER/DB_PASSWORD/DB_NAME 覆盖连接
import mysql from 'mysql2/promise';

const c = await mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '123456',
  database: process.env.DB_NAME || 'bookshop',
  multipleStatements: true
});
const q = async s => (await c.query(s))[0];

// 测试用户（uid>=3）及其全部业务数据
for (const t of ['comment_like', 'comment', 'notification', 'address', 'favorite', 'cart']) {
  await q(`delete from ${t} where uid>=3`);
}
await q('delete from payment where oid in (select oid from `order` where uid>=3)');
await q('delete from order_request where uid>=3');
await q('delete from `order` where uid>=3');
await q('delete from user where uid>=3');
// 冒烟 CRUD 生命周期可能残留的分类/店铺/图书
await q("delete from book where name like '冒烟%'");
await q("delete from shop where name like '冒烟%'");
await q("delete from kind where content like '冒烟%'");
// 演示基线中应为空的日志/验证码表
for (const t of ['operation_log', 'search_log', 'reset_code']) {
  await q(`delete from ${t}`);
}
// 自增序复位（book/shop/kind 按现有最大主键 +1，兼容全量演示数据）
const seq = { user: 3, order: 4, comment: 4 };
for (const [t, n] of Object.entries(seq)) await q(`alter table \`${t}\` auto_increment=${n}`);
for (const [t, pk] of Object.entries({ book: 'bid', shop: 'sid', kind: 'kid' })) {
  const r = await q(`select coalesce(max(${pk}),0)+1 n from ${t}`);
  await q(`alter table \`${t}\` auto_increment=${r[0].n}`);
}

const counts = await q(`select
  (select count(*) from user) users, (select count(*) from \`order\`) orders,
  (select count(*) from book) books, (select count(*) from kind) kinds,
  (select count(*) from shop) shops, (select count(*) from cart) carts,
  (select count(*) from favorite) favs, (select count(*) from notification) noti,
  (select count(*) from operation_log) oplog`);
console.log('清理后基线:', JSON.stringify(counts[0]));
const b1 = await q('select inventory, volume from book where bid=1');
console.log('book1 库存/销量:', JSON.stringify(b1[0]));
await c.end();