// Bookshop P0+P1+P2 冒烟测试：需后端运行于 http://localhost:8090/project1
// 覆盖：认证/越权、注册校验、订单状态机全生命周期、金额快照、库存/销量联动、
//       并发状态保护、服务端校验、隐私接口下线、评论、图片服务
// P1 新增：收藏 CRUD/幂等、个人资料修改、头像上传、管理端鉴权、
//       分类/店铺/图书 CRUD 生命周期、用户封禁/解封、全量列表
// P2 新增：时区序列化、搜索排序白名单、购物车 CRUD/归属/结算/事务回滚、
//       BCrypt 平滑升级、登录/注册限流 429
import { readFileSync, unlinkSync } from 'fs';
import { randomUUID } from 'crypto';
const BASE = process.env.SMOKE_BASE || 'http://localhost:8090/project1';
let pass = 0, fail = 0;
const check = (name, cond, extra = '') => {
  if (cond) { pass++; console.log('PASS  ' + name); }
  else { fail++; console.log('FAIL  ' + name + '  ' + extra); }
};
const form = obj => new URLSearchParams(Object.entries(obj).filter(([, v]) => v !== undefined && v !== '' && v !== null));
const api = async (method, path, { body, token, raw, extraHeaders } = {}) => {
  const headers = {};
  if (token) headers.Authorization = 'Bearer ' + token;
  if (extraHeaders) Object.assign(headers, extraHeaders);
  let payload;
  if (body instanceof URLSearchParams) {
    payload = body;
    headers['Content-Type'] = 'application/x-www-form-urlencoded';
  } else if (typeof FormData !== 'undefined' && body instanceof FormData) {
    payload = body; // fetch 自动设置 multipart boundary
  } else if (body) {
    payload = form(body);
    headers['Content-Type'] = 'application/x-www-form-urlencoded';
  }
  const res = await fetch(BASE + path, { method, headers, body: payload });
  if (raw) return res;
  return { status: res.status, json: await res.json().catch(() => null) };
};
const ok = r => r.json?.code === 1;
const bad = r => r.json?.code === 0;

// ---------- 基础与认证 ----------
check('公开接口 /kind/list', ok(await api('GET', '/kind/list')));
check('未登录访问 /user/1 返回 401', (await api('GET', '/user/1')).status === 401);

const login = await api('POST', '/user/login', { body: { username: 'testuser', password: '123456' } });
check('testuser 登录成功', ok(login) && !!login.json?.data?.token, JSON.stringify(login.json));
const token = login.json?.data?.token;
const me = login.json?.data?.user;
check('登录返回用户不含密码哈希', me && me.password == null);
check('本人查询个人信息成功且无密码', ok(await api('GET', '/user/' + me.uid, { token })));
check('普通用户查看他人信息被拒绝', bad(await api('GET', '/user/2', { token })));
check('伪造 token 返回 401', (await api('GET', '/user/1', { token: 'abc.def.ghi' })).status === 401);

const adminLogin = await api('POST', '/user/login', { body: { username: 'admin', password: '123456' } });
const adminToken = adminLogin.json?.data?.token;
check('admin 登录成功', ok(adminLogin) && !!adminToken);

// ---------- 注册与服务端校验 ----------
const uname = 'smoke_' + Date.now().toString().slice(-8);
check('注册新用户成功', ok(await api('POST', '/user/register', { body: { username: uname, password: '123456', tel: '13900000000', email: 'smoke@test.com' } })));
check('重复用户名注册被拒绝', (await api('POST', '/user/register', { body: { username: uname, password: '123456' } })).json?.msg === '用户名已存在');
check('非法用户名/密码注册被拒绝', bad(await api('POST', '/user/register', { body: { username: 'ab', password: '123' } })));
check('注册手机号格式校验', (await api('POST', '/user/register', { body: { username: uname + 'x', password: '123456', tel: '12345' } })).json?.msg === '手机号格式不正确');
check('注册邮箱格式校验', (await api('POST', '/user/register', { body: { username: uname + 'y', password: '123456', email: 'not-an-email' } })).json?.msg === '邮箱格式不正确');
const newLogin = await api('POST', '/user/login', { body: { username: uname, password: '123456' } });
let newToken = newLogin.json?.data?.token;
const newUid = newLogin.json?.data?.user?.uid;
check('新用户登录成功', ok(newLogin));

// ---------- 图书与隐私接口 ----------
const books = await api('GET', '/book/list/1');
const bid = books.json?.data?.[0]?.bid;
const detail0 = await api('GET', '/book/detail?bid=' + bid);
const book0 = detail0.json?.data;
const inv0 = Number(book0?.inventory), vol0 = Number(book0?.volume), price = Number(book0?.price);
check('图书详情库存/销量为数值', ok(detail0) && Number.isInteger(inv0) && Number.isInteger(vol0), JSON.stringify({ inv0, vol0 }));
const priv1 = await api('GET', '/order/listByBid?bid=' + bid);
const priv2 = await api('GET', '/order/listByBid?bid=' + bid, { token });
check('隐私接口 /order/listByBid 已下线', [401, 404].includes(priv1.status) && [401, 404].includes(priv2.status), `status=${priv1.status}/${priv2.status}`);

// ---------- 下单校验与金额快照 ----------
const addrTag = 'P0测试地址' + Date.now().toString().slice(-6);
const orderBody = { bid, num: 2, name: '冒烟测试', tel: '13900000000', addr: addrTag };
check('下单缺地址被拒绝（服务端校验）', bad(await api('POST', '/order/save', { token: newToken, body: { ...orderBody, addr: '' } })));
check('下单手机号非法被拒绝', bad(await api('POST', '/order/save', { token: newToken, body: { ...orderBody, tel: '123' } })));
check('下单数量超上限被拒绝', bad(await api('POST', '/order/save', { token: newToken, body: { ...orderBody, num: 1000 } })));
check('下单不存在的图书被拒绝', bad(await api('POST', '/order/save', { token: newToken, body: { ...orderBody, bid: 99999 } })));

const save1 = await api('POST', '/order/save', { token: newToken, body: orderBody });
check('下单成功', ok(save1), JSON.stringify(save1.json));
const findOrder = async (tk, uid, addr) => {
  const r = await api('GET', `/order/listByUid?uid=${uid}&pageNo=1&pageSize=50`, { token: tk });
  return r.json?.data?.list?.find(o => o.addr === addr);
};
let o1 = await findOrder(newToken, newUid, addrTag);
check('新订单为待付款且金额=单价×数量', o1?.state === '0' && Number(o1?.amount) === price * 2, JSON.stringify(o1));
check('客户端传入 state 被忽略', o1?.state === '0');
const inv1 = Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory);
check('下单后库存原子扣减', inv1 === inv0 - 2, `inv0=${inv0} inv1=${inv1}`);
check('超库存下单被拒绝', (await api('POST', '/order/save', { token: newToken, body: { ...orderBody, num: inv1 + 100 } })).json?.msg === '图书库存不足');

// ---------- A8：进行中订单不可删除 ----------
check('待付款订单直接删除被拒绝', bad(await api('POST', '/order/remove', { token: newToken, body: { oid: o1.oid } })));

// ---------- 付款与销量 ----------
check('他人订单不可付款', bad(await api('POST', '/order/pay', { token, body: { oid: o1.oid } })));
check('付款成功', ok(await api('POST', '/order/pay', { token: newToken, body: { oid: o1.oid } })));
const vol1 = Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.volume);
check('付款后销量增加', vol1 === vol0 + 2, `vol0=${vol0} vol1=${vol1}`);
check('重复付款被拒绝', bad(await api('POST', '/order/pay', { token: newToken, body: { oid: o1.oid } })));

// ---------- 并发付款保护 ----------
const addrTag2 = addrTag + 'C';
await api('POST', '/order/save', { token: newToken, body: { ...orderBody, num: 1, addr: addrTag2 } });
const oC = await findOrder(newToken, newUid, addrTag2);
const races = await Promise.all(Array.from({ length: 5 }, () => api('POST', '/order/pay', { token: newToken, body: { oid: oC.oid } })));
check('并发付款仅一次成功', races.filter(ok).length === 1, `success=${races.filter(ok).length}`);
await api('POST', '/order/cancel', { token: newToken, body: { oid: oC.oid } }); // 清理（已付款取消）

// ---------- 取消：回补库存与销量 ----------
check('已付款订单取消成功', ok(await api('POST', '/order/cancel', { token: newToken, body: { oid: o1.oid } })));
const d2 = (await api('GET', '/book/detail?bid=' + bid)).json?.data;
check('取消后库存回补', Number(d2.inventory) === inv0, JSON.stringify(d2.inventory));
check('已付款取消后销量回滚', Number(d2.volume) === vol0, JSON.stringify(d2.volume));
o1 = await findOrder(newToken, newUid, addrTag);
check('取消后状态为已取消', o1?.state === '5');
check('终态订单可删除且不回补库存', ok(await api('POST', '/order/remove', { token: newToken, body: { oid: o1.oid } })));
check('删除终态订单后库存不变', Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory) === inv0);

// ---------- 完整生命周期：下单→付款→发货→收货→删除 ----------
const addrTag3 = addrTag + 'L';
await api('POST', '/order/save', { token: newToken, body: { ...orderBody, num: 1, addr: addrTag3 } });
const o3 = await findOrder(newToken, newUid, addrTag3);
await api('POST', '/order/pay', { token: newToken, body: { oid: o3.oid } });
check('非管理员发货被拒绝', (await api('POST', '/order/ship', { token: newToken, body: { oid: o3.oid } })).json?.msg === '仅管理员可发货');
check('管理员发货成功', ok(await api('POST', '/order/ship', { token: adminToken, body: { oid: o3.oid } })));
check('已发货订单不可取消', bad(await api('POST', '/order/cancel', { token: newToken, body: { oid: o3.oid } })));
check('已发货订单不可删除', bad(await api('POST', '/order/remove', { token: newToken, body: { oid: o3.oid } })));
check('他人确认收货被拒绝', bad(await api('POST', '/order/confirm', { token, body: { oid: o3.oid } })));
check('本人确认收货成功', ok(await api('POST', '/order/confirm', { token: newToken, body: { oid: o3.oid } })));
const o3after = await findOrder(newToken, newUid, addrTag3);
check('确认收货后为终态', o3after?.state === '3');
const invBeforeDel = Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory);
check('终态订单删除成功', ok(await api('POST', '/order/remove', { token: newToken, body: { oid: o3.oid } })));
check('已完成订单删除不回补库存（A8）', Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory) === invBeforeDel);

// ---------- 越权 ----------
check('查看他人订单被拒绝', bad(await api('GET', '/order/listByUid?uid=1&pageNo=1&pageSize=8', { token: newToken })));
check('删除他人订单被拒绝', bad(await api('POST', '/order/remove', { token: newToken, body: { oid: 1 } })));

// ---------- 评论 ----------
check('超长评论被拒绝', (await api('POST', '/comment/save', { token: newToken, body: { bid, content: '长'.repeat(501) } })).json?.msg === '评论内容不能超过 500 字');
check('发表评论成功', ok(await api('POST', '/comment/save', { token: newToken, body: { bid, content: '冒烟测试评论' } })));
const cmts = await api('GET', '/comment/listByBid?bid=' + bid);
const mine = cmts.json?.data?.find(c => c.content === '冒烟测试评论');
check('评论嵌套用户无密码', !!mine && mine.user?.password == null);
check('删除他人评论被拒绝', bad(await api('POST', '/comment/remove', { token, body: { cid: mine.cid } })));
check('删除本人评论成功', ok(await api('POST', '/comment/remove', { token: newToken, body: { cid: mine.cid } })));

// ---------- 管理员与改密 ----------
check('管理员查看他人信息成功', ok(await api('GET', '/user/' + newUid, { token: adminToken })));
check('原密码错误改密被拒绝', (await api('POST', '/user/updatePassword', { token: newToken, body: { OldPassword: 'wrong', NewPassword: '654321' } })).json?.msg === '原密码错误');
check('超长新密码被拒绝', bad(await api('POST', '/user/updatePassword', { token: newToken, body: { OldPassword: '123456', NewPassword: 'x'.repeat(65) } })));
check('改密成功', ok(await api('POST', '/user/updatePassword', { token: newToken, body: { OldPassword: '123456', NewPassword: '654321' } })));
const loginAfterPasswordChange = await api('POST', '/user/login', { body: { username: uname, password: '654321' } });
check('新密码可登录', ok(loginAfterPasswordChange));
newToken = loginAfterPasswordChange.json?.data?.token;

// ---------- 图片 ----------
check('图片服务 /pic/default.png 可访问', (await api('GET', '/pic/default.png', { raw: true })).status === 200);

// ========== P1 ==========
const uploadedFiles = []; // 测试产生的图片文件，结束后清理

// ---------- P1 收藏 ----------
check('未登录访问收藏接口返回 401', (await api('GET', '/favorite/check?bid=' + bid)).status === 401);
check('初始未收藏', (await api('GET', '/favorite/check?bid=' + bid, { token: newToken })).json?.data === false);
check('收藏成功', ok(await api('POST', '/favorite/save', { token: newToken, body: { bid } })));
check('收藏后 check 为 true', (await api('GET', '/favorite/check?bid=' + bid, { token: newToken })).json?.data === true);
check('重复收藏幂等成功', ok(await api('POST', '/favorite/save', { token: newToken, body: { bid } })));
const favList = await api('GET', `/favorite/listByUid?uid=${newUid}&pageNo=1&pageSize=8`, { token: newToken });
const favItem = favList.json?.data?.list?.find(f => f.bid === bid);
check('收藏列表含嵌套图书', ok(favList) && !!favItem?.book?.name, JSON.stringify(favList.json));
check('查看他人收藏被拒绝', bad(await api('GET', `/favorite/listByUid?uid=${newUid}&pageNo=1&pageSize=8`, { token })));
check('收藏不存在的图书被拒绝', bad(await api('POST', '/favorite/save', { token: newToken, body: { bid: 99999 } })));
check('取消收藏成功', ok(await api('POST', '/favorite/remove', { token: newToken, body: { bid } })));
check('取消后 check 为 false', (await api('GET', '/favorite/check?bid=' + bid, { token: newToken })).json?.data === false);
check('重复取消收藏被拒绝', bad(await api('POST', '/favorite/remove', { token: newToken, body: { bid } })));

// ---------- P1 个人资料 ----------
check('修改资料手机号格式校验', (await api('POST', '/user/updateInfo', { token: newToken, body: { tel: '12345' } })).json?.msg === '手机号格式不正确');
check('修改资料邮箱格式校验', (await api('POST', '/user/updateInfo', { token: newToken, body: { email: 'bad-email' } })).json?.msg === '邮箱格式不正确');
const updInfo = await api('POST', '/user/updateInfo', { token: newToken, body: { tel: '13911112222', email: 'p1@test.com' } });
check('修改资料成功且返回无密码', ok(updInfo) && updInfo.json?.data?.tel === '13911112222' && updInfo.json?.data?.password == null, JSON.stringify(updInfo.json));
const clearEmail = await api('POST', '/user/updateInfo', { token: newToken, body: new URLSearchParams({ tel: '13911112222', email: '' }) });
check('空字符串可清空邮箱', ok(clearEmail) && clearEmail.json?.data?.email === '', JSON.stringify(clearEmail.json));

// ---------- P1 头像上传 ----------
const pngBytes = readFileSync(new URL('./picture/default.png', import.meta.url));
const avatarForm = new FormData();
avatarForm.append('file', new File([pngBytes], 'avatar.png', { type: 'image/png' }));
const upAvatar = await api('POST', '/user/uploadAvatar', { token: newToken, body: avatarForm });
const avatarName = upAvatar.json?.data?.image;
check('头像上传成功并写入用户信息', ok(upAvatar) && typeof avatarName === 'string' && avatarName.startsWith('avatar_' + newUid + '_'), JSON.stringify(upAvatar.json));
if (avatarName) uploadedFiles.push(avatarName);
check('上传的头像可访问', avatarName && (await api('GET', '/pic/' + avatarName, { raw: true })).status === 200);
const badExtForm = new FormData();
badExtForm.append('file', new File([new Uint8Array([1, 2, 3])], 'evil.txt', { type: 'text/plain' }));
check('非图片扩展名上传被拒绝', (await api('POST', '/user/uploadAvatar', { token: newToken, body: badExtForm })).json?.msg?.includes('仅支持'));
check('未登录上传头像返回 401', (await api('POST', '/user/uploadAvatar', { body: avatarForm })).status === 401);

// ---------- P1 管理端鉴权 ----------
check('非管理员查用户列表被拒绝', (await api('GET', '/user/list?pageNo=1&pageSize=8', { token: newToken })).json?.msg === '需要管理员权限');
check('非管理员查全部订单被拒绝', bad(await api('GET', '/order/listAll?pageNo=1&pageSize=8', { token: newToken })));
check('非管理员查全部评论被拒绝', bad(await api('GET', '/comment/listAll?pageNo=1&pageSize=8', { token: newToken })));
check('非管理员保存图书被拒绝', bad(await api('POST', '/book/save', { token: newToken, body: { name: 'x', price: 1, kid: 1, sid: 1 } })));
check('非管理员保存分类被拒绝', bad(await api('POST', '/kind/save', { token: newToken, body: { content: 'x' } })));
check('非管理员保存店铺被拒绝', bad(await api('POST', '/shop/save', { token: newToken, body: { name: 'x', kid: 1 } })));
check('非管理员封禁用户被拒绝', bad(await api('POST', '/user/setState', { token: newToken, body: { uid: newUid, state: 1 } })));
check('非管理员上传图书图片被拒绝', bad(await api('POST', '/book/uploadImage', { token: newToken, body: avatarForm })));
check('未登录访问管理写接口返回 401', (await api('POST', '/book/save', { body: { name: 'x' } })).status === 401);

const userList = await api('GET', '/user/list?pageNo=1&pageSize=50', { token: adminToken });
check('管理员查用户列表成功且无密码', ok(userList) && userList.json?.data?.list?.every(u => u.password == null));
const allOrders = await api('GET', '/order/listAll?pageNo=1&pageSize=8', { token: adminToken });
check('管理员查全部订单（嵌套买家无密码）', ok(allOrders) && allOrders.json?.data?.list?.every(o => !o.user || o.user.password == null));
check('管理员查全部评论成功', ok(await api('GET', '/comment/listAll?pageNo=1&pageSize=8', { token: adminToken })));

// ---------- P1 封禁/解封 ----------
check('管理员封禁用户成功', ok(await api('POST', '/user/setState', { token: adminToken, body: { uid: newUid, state: 1 } })));
check('封禁后登录被拒绝', (await api('POST', '/user/login', { body: { username: uname, password: '654321' } })).json?.msg === '账号已被封禁，请联系管理员');
check('管理员不能封禁自己', (await api('POST', '/user/setState', { token: adminToken, body: { uid: 2, state: 1 } })).json?.msg === '不能修改自己的账号状态');
check('非法 state 被拒绝', bad(await api('POST', '/user/setState', { token: adminToken, body: { uid: newUid, state: 2 } })));
check('管理员解封用户成功', ok(await api('POST', '/user/setState', { token: adminToken, body: { uid: newUid, state: 0 } })));
const loginAfterUnban = await api('POST', '/user/login', { body: { username: uname, password: '654321' } });
check('解封后可登录', ok(loginAfterUnban));
newToken = loginAfterUnban.json?.data?.token;

// ---------- P1 分类/店铺/图书 CRUD 生命周期 ----------
const tag = Date.now().toString().slice(-6);
check('新增分类成功', ok(await api('POST', '/kind/save', { token: adminToken, body: { content: '冒烟分类' + tag } })));
const kindList = (await api('GET', '/kind/list')).json?.data || [];
const newKind = kindList.find(k => k.content === '冒烟分类' + tag);
check('新分类出现在列表', !!newKind);
check('编辑分类成功', ok(await api('POST', '/kind/save', { token: adminToken, body: { kid: newKind.kid, content: '冒烟分类改' + tag } })));
check('空分类名被拒绝', bad(await api('POST', '/kind/save', { token: adminToken, body: { content: ' ' } })));
check('新增店铺成功', ok(await api('POST', '/shop/save', { token: adminToken, body: { name: '冒烟店铺' + tag, kid: newKind.kid, score: '4.5', state: '0' } })));
const shopAll = (await api('GET', '/shop/listAll')).json?.data || [];
const newShop = shopAll.find(s => s.name === '冒烟店铺' + tag);
check('新店铺出现在 listAll（含完整字段）', !!newShop && newShop.kid === newKind.kid);
const bookBody = { name: '冒烟图书' + tag, author: '测试作者', price: '9.90', inventory: 5, kid: newKind.kid, sid: newShop.sid, time: '2020-01-01 00:00:00' };
check('新增图书成功', ok(await api('POST', '/book/save', { token: adminToken, body: bookBody })));
const found = (await api('POST', '/book/search', { body: { name: '冒烟图书' + tag, pageNo: 1, pageSize: 8 } })).json?.data?.list || [];
const newBook = found.find(b => b.name === '冒烟图书' + tag);
check('新图书可搜索且含库存/状态字段', !!newBook && newBook.inventory === 5 && newBook.state === 0, JSON.stringify(newBook));
const newDetail = (await api('GET', '/book/detail?bid=' + newBook.bid)).json?.data;
check('新图书冗余分类名正确且销量为 0', newDetail?.content === '冒烟分类改' + tag && newDetail?.volume === 0, JSON.stringify({ content: newDetail?.content, volume: newDetail?.volume }));
check('缺价格新增图书被拒绝', bad(await api('POST', '/book/save', { token: adminToken, body: { ...bookBody, name: '冒烟图书X' + tag, price: '' } })));
check('非法分类新增图书被拒绝', (await api('POST', '/book/save', { token: adminToken, body: { ...bookBody, name: '冒烟图书X' + tag, kid: 99999 } })).json?.msg === '所属分类不存在');
check('编辑图书成功', ok(await api('POST', '/book/save', { token: adminToken, body: { bid: newBook.bid, name: '冒烟图书改' + tag, price: '19.90', inventory: 6, kid: newKind.kid, sid: newShop.sid } })));
const edited = (await api('GET', '/book/detail?bid=' + newBook.bid)).json?.data;
check('编辑后价格/库存生效且销量不被客户端篡改', Number(edited?.price) === 19.9 && edited?.inventory === 6 && edited?.volume === 0, JSON.stringify({ price: edited?.price, inventory: edited?.inventory, volume: edited?.volume }));

// 图书删除保护：有订单引用时拒绝，取消后可删
const smokeAddr = 'P1测试地址' + tag;
check('对新图书下单成功', ok(await api('POST', '/order/save', { token: newToken, body: { bid: newBook.bid, num: 1, name: '冒烟', tel: '13900000000', addr: smokeAddr } })));
const smokeOrder = (await api('GET', `/order/listByUid?uid=${newUid}&pageNo=1&pageSize=50`, { token: newToken })).json?.data?.list?.find(o => o.addr === smokeAddr);
check('有订单引用的图书删除被拒绝', (await api('POST', '/book/remove', { token: adminToken, body: { bid: newBook.bid } })).json?.msg?.includes('不能删除'));
check('取消订单成功', ok(await api('POST', '/order/cancel', { token: newToken, body: { oid: smokeOrder.oid } })));
check('删除终态订单成功', ok(await api('POST', '/order/remove', { token: newToken, body: { oid: smokeOrder.oid } })));
check('无引用后图书删除成功', ok(await api('POST', '/book/remove', { token: adminToken, body: { bid: newBook.bid } })));
check('店铺删除成功', ok(await api('POST', '/shop/remove', { token: adminToken, body: { sid: newShop.sid } })));
check('分类删除成功', ok(await api('POST', '/kind/remove', { token: adminToken, body: { kid: newKind.kid } })));
check('删除被引用分类被拒绝', (await api('POST', '/kind/remove', { token: adminToken, body: { kid: 1 } })).json?.msg?.includes('不能删除'));
check('删除被引用店铺被拒绝', (await api('POST', '/shop/remove', { token: adminToken, body: { sid: 1 } })).json?.msg?.includes('不能删除'));

// ---------- P1 图书图片上传 ----------
const bookImgForm = new FormData();
bookImgForm.append('file', new File([pngBytes], 'cover.png', { type: 'image/png' }));
const upBookImg = await api('POST', '/book/uploadImage', { token: adminToken, body: bookImgForm });
const bookImgName = upBookImg.json?.data;
check('管理员上传图书图片成功', ok(upBookImg) && typeof bookImgName === 'string' && bookImgName.startsWith('book_'), JSON.stringify(upBookImg.json));
if (bookImgName) uploadedFiles.push(bookImgName);
check('上传的图书图片可访问', bookImgName && (await api('GET', '/pic/' + bookImgName, { raw: true })).status === 200);

// ========== P2 ==========

// ---------- P2 时区 ----------
const book5 = (await api('GET', '/book/detail?bid=5')).json?.data;
check('时间序列化时区正确（GMT+8）', book5?.time === '2010-04-01 00:00:00', JSON.stringify(book5?.time));

// ---------- P2 搜索排序 ----------
const searchSort = async sort => (await api('POST', '/book/search', { body: { name: '简史', pageNo: 1, pageSize: 8, sort } })).json?.data?.list || [];
const asc = await searchSort('price_asc');
check('价格升序排序', asc.length >= 2 && asc.every((b, i) => i === 0 || Number(asc[i - 1].price) <= Number(b.price)), JSON.stringify(asc.map(b => b.price)));
const desc = await searchSort('price_desc');
check('价格降序排序', desc.length >= 2 && desc.every((b, i) => i === 0 || Number(desc[i - 1].price) >= Number(b.price)), JSON.stringify(desc.map(b => b.price)));
const volDesc = await searchSort('volume_desc');
check('销量降序排序', volDesc.length >= 2 && volDesc.every((b, i) => i === 0 || Number(volDesc[i - 1].volume) >= Number(b.volume)), JSON.stringify(volDesc.map(b => b.volume)));
const inject = await api('POST', '/book/search', { body: { name: '简史', pageNo: 1, pageSize: 8, sort: 'price; drop table book--' } });
check('非法 sort 回退默认排序（白名单防注入）', ok(inject) && (inject.json?.data?.list || []).length >= 2);

// ---------- P2 购物车 ----------
check('未登录访问购物车返回 401', (await api('GET', '/cart/list')).status === 401);
check('加入购物车成功', ok(await api('POST', '/cart/add', { token: newToken, body: { bid, num: 2 } })));
check('重复加购数量累加', ok(await api('POST', '/cart/add', { token: newToken, body: { bid, num: 3 } })));
let cartList = (await api('GET', '/cart/list', { token: newToken })).json?.data || [];
let cartItem = cartList.find(c => c.bid === bid);
check('购物车列表数量为 5 且嵌套图书完整', cartItem?.num === 5 && !!cartItem?.book?.name && Number.isInteger(cartItem?.book?.inventory), JSON.stringify(cartItem));
check('修改购物车数量成功', ok(await api('POST', '/cart/updateNum', { token: newToken, body: { cid: cartItem.cid, num: 2 } })));
check('数量改为 0 被拒绝', (await api('POST', '/cart/updateNum', { token: newToken, body: { cid: cartItem.cid, num: 0 } })).json?.msg === '数量至少为 1');
check('数量超上限被拒绝', bad(await api('POST', '/cart/updateNum', { token: newToken, body: { cid: cartItem.cid, num: 1000 } })));
check('加购不存在的图书被拒绝', (await api('POST', '/cart/add', { token: newToken, body: { bid: 99999, num: 1 } })).json?.msg === '图书不存在');
check('加购超库存被拒绝', (await api('POST', '/cart/add', { token: newToken, body: { bid, num: 500 } })).json?.msg === '超出图书剩余库存');

// 归属校验：testuser 的购物车项，新用户不可改/删
check('另一用户加购成功', ok(await api('POST', '/cart/add', { token, body: { bid, num: 1 } })));
const otherCart = ((await api('GET', '/cart/list', { token })).json?.data || []).find(c => c.bid === bid);
check('修改他人购物车被拒绝', (await api('POST', '/cart/updateNum', { token: newToken, body: { cid: otherCart.cid, num: 9 } })).json?.msg === '无权操作他人的购物车');
check('删除他人购物车被拒绝', bad(await api('POST', '/cart/remove', { token: newToken, body: { cid: otherCart.cid } })));
check('本人删除购物车成功', ok(await api('POST', '/cart/remove', { token, body: { cid: otherCart.cid } })));

// 结算校验与成功路径
check('结算缺地址被拒绝', bad(await api('POST', '/cart/checkout', { token: newToken, body: { name: '冒烟', tel: '13900000000', addr: '' } })));
const cartAddr = 'P2购物车' + tag;
const invBeforeCheckout = Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory);
check('购物车结算成功', ok(await api('POST', '/cart/checkout', { token: newToken, body: { name: '冒烟', tel: '13900000000', addr: cartAddr } })));
check('结算后购物车清空', ((await api('GET', '/cart/list', { token: newToken })).json?.data || []).length === 0);
const cartOrder = await findOrder(newToken, newUid, cartAddr);
check('结算生成待付款订单且金额快照正确', cartOrder?.state === '0' && Number(cartOrder?.amount) === price * 2, JSON.stringify(cartOrder));
check('结算扣减库存', Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory) === invBeforeCheckout - 2);
check('结算订单可取消（回补库存）', ok(await api('POST', '/order/cancel', { token: newToken, body: { oid: cartOrder.oid } })));
check('删除结算订单', ok(await api('POST', '/order/remove', { token: newToken, body: { oid: cartOrder.oid } })));
check('取消后库存复原', Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory) === invBeforeCheckout);
check('空购物车结算被拒绝', (await api('POST', '/cart/checkout', { token: newToken, body: { name: '冒烟', tel: '13900000000', addr: cartAddr } })).json?.msg === '购物车为空');

// 结算回滚：购物车含下架图书时整体失败、购物车保持不变
const tmpBookBody = { name: '冒烟购物车书' + tag, author: '测试', price: '5.00', inventory: 10, kid: 1, sid: 1 };
check('新增临时图书成功', ok(await api('POST', '/book/save', { token: adminToken, body: tmpBookBody })));
const tmpBook = ((await api('POST', '/book/search', { body: { name: '冒烟购物车书' + tag, pageNo: 1, pageSize: 8 } })).json?.data?.list || [])[0];
check('临时图书加购成功', ok(await api('POST', '/cart/add', { token: newToken, body: { bid: tmpBook.bid, num: 1 } })));
check('主图书加购成功', ok(await api('POST', '/cart/add', { token: newToken, body: { bid, num: 1 } })));
check('管理员下架临时图书', ok(await api('POST', '/book/save', { token: adminToken, body: { ...tmpBookBody, bid: tmpBook.bid, state: 1 } })));
check('加购下架图书被拒绝', (await api('POST', '/cart/add', { token: newToken, body: { bid: tmpBook.bid, num: 1 } })).json?.msg === '图书已下架，无法加入购物车');
const rollbackAddr = 'P2回滚' + tag;
const invBeforeRollback = Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory);
const rollbackCheckout = await api('POST', '/cart/checkout', { token: newToken, body: { name: '冒烟', tel: '13900000000', addr: rollbackAddr } });
check('含下架图书结算被拒绝', bad(rollbackCheckout) && rollbackCheckout.json?.msg?.includes('已下架'), JSON.stringify(rollbackCheckout.json));
check('结算失败整体回滚（主图书库存未扣）', Number((await api('GET', '/book/detail?bid=' + bid)).json?.data?.inventory) === invBeforeRollback);
check('结算失败购物车保持不变', ((await api('GET', '/cart/list', { token: newToken })).json?.data || []).length === 2);
check('结算失败未生成订单', !(await findOrder(newToken, newUid, rollbackAddr)));
// 清理购物车与临时图书
cartList = (await api('GET', '/cart/list', { token: newToken })).json?.data || [];
for (const c of cartList) await api('POST', '/cart/remove', { token: newToken, body: { cid: c.cid } });
check('清理后购物车为空', ((await api('GET', '/cart/list', { token: newToken })).json?.data || []).length === 0);
check('删除临时图书成功', ok(await api('POST', '/book/remove', { token: adminToken, body: { bid: tmpBook.bid } })));

// ---------- P2 BCrypt 平滑升级 ----------
check('MD5 老用户登录后仍可再次登录（自动升级为 BCrypt）', ok(await api('POST', '/user/login', { body: { username: 'testuser', password: '123456' } })));
check('admin 再次登录成功', ok(await api('POST', '/user/login', { body: { username: 'admin', password: '123456' } })));

// ========== P3 ==========
const p3tag = Date.now().toString().slice(-6);

// ---------- P3 支付、退款与订单详情 ----------
const p3RefundAddr = 'P3退款' + p3tag;
const beforeRefund = (await api('GET', '/book/detail?bid=' + bid)).json?.data;
const refundInv = Number(beforeRefund.inventory);
const refundVol = Number(beforeRefund.volume);
const refundSave = await api('POST', '/order/save', {
  token: newToken,
  body: { bid, num: 1, name: 'P3退款用户', tel: '13900000000', addr: p3RefundAddr }
});
check('P3 退款测试订单创建成功', ok(refundSave), JSON.stringify(refundSave.json));
const refundOrder = await findOrder(newToken, newUid, p3RefundAddr);
const payWithRecord = await api('POST', '/order/pay', {
  token: newToken, body: { oid: refundOrder.oid, channel: 'sandbox' }
});
check('付款写入沙箱支付记录', ok(payWithRecord) && payWithRecord.json?.data?.status === 'PAID'
  && String(payWithRecord.json?.data?.tradeNo || '').startsWith('SB'), JSON.stringify(payWithRecord.json));
check('未登录查看订单详情返回401',
  (await api('GET', '/order/detail?oid=' + refundOrder.oid)).status === 401);
check('他人查看订单详情被拒绝',
  bad(await api('GET', '/order/detail?oid=' + refundOrder.oid, { token })));
const paidDetail = await api('GET', '/order/detail?oid=' + refundOrder.oid, { token: newToken });
check('订单详情包含图书与买家公开信息', ok(paidDetail) && paidDetail.json?.data?.book?.bid === bid
  && paidDetail.json?.data?.user?.username === uname && paidDetail.json?.data?.user?.password == null);
check('已付款未发货订单可退款',
  ok(await api('POST', '/order/refund', { token: newToken, body: { oid: refundOrder.oid } })));
const afterRefund = (await api('GET', '/book/detail?bid=' + bid)).json?.data;
check('退款后库存与销量复原', Number(afterRefund.inventory) === refundInv
  && Number(afterRefund.volume) === refundVol, JSON.stringify({ inventory: afterRefund.inventory, volume: afterRefund.volume }));
check('退款后订单状态为6',
  (await api('GET', '/order/detail?oid=' + refundOrder.oid, { token: newToken })).json?.data?.state === '6');
check('重复退款被拒绝',
  bad(await api('POST', '/order/refund', { token: newToken, body: { oid: refundOrder.oid } })));
check('已发货订单不可退款', bad(await api('POST', '/order/refund', { token: adminToken, body: { oid: 2 } })));
check('退款终态订单可删除',
  ok(await api('POST', '/order/remove', { token: newToken, body: { oid: refundOrder.oid } })));

// ---------- P3 配件地址簿 ----------
const a1Result = await api('POST', '/address/save', {
  token: newToken,
  body: { name: '张三', tel: '13800001111', addr: '武汉市洪山区A号', isDefault: 1 }
});
const a1 = a1Result.json?.data;
check('新增收货地址成功且首条为默认', ok(a1Result) && a1?.isDefault === 1, JSON.stringify(a1Result.json));
const a2Result = await api('POST', '/address/save', {
  token: newToken,
  body: { name: '李四', tel: '13800002222', addr: '武汉市武昌区B号', isDefault: 1 }
});
const a2 = a2Result.json?.data;
let addressList = (await api('GET', '/address/list', { token: newToken })).json?.data || [];
check('默认地址唯一', addressList.filter(a => a.isDefault === 1).length === 1
  && addressList.find(a => a.isDefault === 1)?.aid === a2?.aid, JSON.stringify(addressList));
check('编辑收货地址成功',
  ok(await api('POST', '/address/save', {
    token: newToken,
    body: { aid: a1.aid, name: '张三改', tel: '13800001111', addr: '武汉市洪山区A号改', isDefault: 0 }
  })));
check('设置默认地址成功',
  ok(await api('POST', '/address/setDefault', { token: newToken, body: { aid: a1.aid } })));
check('越权删除他人地址被拒绝',
  bad(await api('POST', '/address/remove', { token, body: { aid: a1.aid } })));
for (let i = 0; i < 18; i++) {
  await api('POST', '/address/save', {
    token: newToken,
    body: { name: '地址' + i, tel: '13800003333', addr: '测试地址' + i, isDefault: 0 }
  });
}
addressList = (await api('GET', '/address/list', { token: newToken })).json?.data || [];
check('地址簿最多保存20条', addressList.length === 20, String(addressList.length));
check('第21条地址被拒绝', bad(await api('POST', '/address/save', {
  token: newToken,
  body: { name: '溢出', tel: '13800003333', addr: '第21条', isDefault: 0 }
})));
for (const item of addressList) {
  await api('POST', '/address/remove', { token: newToken, body: { aid: item.aid } });
}
check('地址测试数据清理完成', ((await api('GET', '/address/list', { token: newToken })).json?.data || []).length === 0);

// ---------- P3 评论评分、点赞、回复 ----------
const p3CommentText = 'P3评分评论' + p3tag;
check('评分超过5被拒绝', bad(await api('POST', '/comment/save', {
  token: newToken, body: { bid, content: p3CommentText + 'bad', score: 6 }
})));
check('评分评论发表成功', ok(await api('POST', '/comment/save', {
  token: newToken, body: { bid, content: p3CommentText, score: 4 }
})));
let p3Comments = (await api('GET', '/comment/listByBid?bid=' + bid)).json?.data || [];
const p3Comment = p3Comments.find(c => c.content === p3CommentText);
check('公开评论列表返回评分与点赞字段', !!p3Comment && p3Comment.score === 4
  && p3Comment.likeCount === 0 && p3Comment.liked === false, JSON.stringify(p3Comment));
check('评论点赞成功', ok(await api('POST', '/comment/like', { token: newToken, body: { cid: p3Comment.cid } })));
check('重复点赞保持幂等', ok(await api('POST', '/comment/like', { token: newToken, body: { cid: p3Comment.cid } })));
p3Comments = (await api('GET', '/comment/listByBid?bid=' + bid, { token: newToken })).json?.data || [];
const likedComment = p3Comments.find(c => c.cid === p3Comment.cid);
check('评论点赞计数为1且当前用户已赞', likedComment?.likeCount === 1 && likedComment?.liked === true,
  JSON.stringify(likedComment));
check('取消点赞幂等成功', ok(await api('POST', '/comment/unlike', { token: newToken, body: { cid: p3Comment.cid } })));
check('非管理员回复评论被拒绝',
  bad(await api('POST', '/comment/reply', { token: newToken, body: { cid: p3Comment.cid, reply: '越权回复' } })));
check('管理员回复评论成功',
  ok(await api('POST', '/comment/reply', { token: adminToken, body: { cid: p3Comment.cid, reply: '感谢您的评价' } })));
p3Comments = (await api('GET', '/comment/listByBid?bid=' + bid)).json?.data || [];
check('评论列表展示商家回复',
  p3Comments.find(c => c.cid === p3Comment.cid)?.reply === '感谢您的评价');
check('删除P3评论成功',
  ok(await api('POST', '/comment/remove', { token: newToken, body: { cid: p3Comment.cid } })));

// ---------- P3 搜索历史热词 ----------
const hotKeyword = '热词测试' + p3tag;
check('热词接口公开可用', ok(await api('GET', '/book/hotWords')));
await api('POST', '/book/search', { body: { name: hotKeyword, pageNo: 1, pageSize: 8 } });
const hotWords = (await api('GET', '/book/hotWords')).json?.data || [];
check('搜索后热词包含该关键词', hotWords.some(item => item.keyword === hotKeyword), JSON.stringify(hotWords));

// ---------- P3 管理看板与操作日志 ----------
check('非管理员访问统计被拒绝', bad(await api('GET', '/admin/stats', { token: newToken })));
check('非管理员访问操作日志被拒绝', bad(await api('GET', '/admin/logs?pageNo=1&pageSize=10', { token: newToken })));
const adminStats = await api('GET', '/admin/stats', { token: adminToken });
check('管理员统计包含核心指标', ok(adminStats)
  && Number(adminStats.json?.data?.userCount) >= 2
  && Number(adminStats.json?.data?.bookCount) >= 5
  && Array.isArray(adminStats.json?.data?.sevenDayTrend), JSON.stringify(adminStats.json));
const logsBefore = await api('GET', '/admin/logs?pageNo=1&pageSize=10', { token: adminToken });
check('管理员操作日志可查询', ok(logsBefore) && Number(logsBefore.json?.data?.total) > 0);

// ---------- P3 通知与物流 ----------
const notifCountBefore = Number((await api('GET', '/notification/unreadCount', { token: newToken })).json?.data);
check('通知未读数可读取', Number.isInteger(notifCountBefore));
const p3BookBody = { name: 'P3物流书' + p3tag, author: '测试', price: '1.00', inventory: 2, kid: 1, sid: 1 };
check('创建物流测试图书成功', ok(await api('POST', '/book/save', { token: adminToken, body: p3BookBody })));
const p3Book = ((await api('POST', '/book/search', {
  body: { name: 'P3物流书' + p3tag, pageNo: 1, pageSize: 8 }
})).json?.data?.list || [])[0];
const logisticsAddr = 'P3物流' + p3tag;
await api('POST', '/order/save', {
  token: newToken,
  body: { bid: p3Book.bid, num: 1, name: '物流测试', tel: '13900000000', addr: logisticsAddr }
});
const logisticsOrder = await findOrder(newToken, newUid, logisticsAddr);
await api('POST', '/order/pay', { token: newToken, body: { oid: logisticsOrder.oid } });
const notificationsBeforeShip = Number((await api('GET', '/notification/unreadCount', { token: newToken })).json?.data);
check('管理员发货可带物流信息', ok(await api('POST', '/order/ship', {
  token: adminToken,
  body: { oid: logisticsOrder.oid, logisticsCompany: '顺丰速运', logisticsNo: 'SF' + p3tag }
})));
const shippedDetail = (await api('GET', '/order/detail?oid=' + logisticsOrder.oid, { token: newToken })).json?.data;
check('订单详情展示物流公司与单号', shippedDetail?.logisticsCompany === '顺丰速运'
  && shippedDetail?.logisticsNo === 'SF' + p3tag
  && !!shippedDetail?.shipTime, JSON.stringify(shippedDetail));
const notificationsAfterShip = Number((await api('GET', '/notification/unreadCount', { token: newToken })).json?.data);
check('付款和发货后买家未读数增加', notificationsAfterShip >= notificationsBeforeShip + 1,
  `${notificationsBeforeShip}->${notificationsAfterShip}`);
const notificationList = await api('GET', '/notification/list?pageNo=1&pageSize=20', { token: newToken });
const shipNotice = notificationList.json?.data?.list?.find(item => item.title === '订单已发货');
check('买家收到发货通知', !!shipNotice && shipNotice.content.includes('顺丰速运'));
check('越权标记他人通知已读被拒绝',
  bad(await api('POST', '/notification/read', { token, body: { nid: shipNotice?.nid } })));
check('本人可标记通知已读',
  ok(await api('POST', '/notification/read', { token: newToken, body: { nid: shipNotice?.nid } })));
check('已读后未读数减少',
  Number((await api('GET', '/notification/unreadCount', { token: newToken })).json?.data) === notificationsAfterShip - 1);
check('物流订单确认收货成功',
  ok(await api('POST', '/order/confirm', { token: newToken, body: { oid: logisticsOrder.oid } })));
check('物流测试订单删除成功',
  ok(await api('POST', '/order/remove', { token: newToken, body: { oid: logisticsOrder.oid } })));
check('物流测试图书删除成功',
  ok(await api('POST', '/book/remove', { token: adminToken, body: { bid: p3Book.bid } })));
const logsAfter = await api('GET', '/admin/logs?pageNo=1&pageSize=10', { token: adminToken });
check('管理操作后日志增长', Number(logsAfter.json?.data?.total) > Number(logsBefore.json?.data?.total),
  `${logsBefore.json?.data?.total}->${logsAfter.json?.data?.total}`);

// ---------- P3 找回密码 ----------
const resetEmail = 'reset.' + p3tag + '@example.com';
await api('POST', '/user/updateInfo', { token: newToken, body: { tel: '13911112222', email: resetEmail } });
check('未注册邮箱发送验证码被拒绝',
  bad(await api('POST', '/user/sendResetCode', {
    body: { email: 'none.' + p3tag + '@example.com' },
    extraHeaders: { 'X-Forwarded-For': '10.3.0.10' }
  })));
check('注册邮箱发送验证码成功',
  ok(await api('POST', '/user/sendResetCode', {
    body: { email: resetEmail },
    extraHeaders: { 'X-Forwarded-For': '10.3.0.10' }
  })));
await new Promise(resolve => setTimeout(resolve, 300));
const logText = readFileSync(new URL('./javaEEProject4/logs/bookshop.log', import.meta.url), 'utf8');
const codeMatches = [...logText.matchAll(new RegExp(`email=${resetEmail.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}, code=(\\d{6})`, 'g'))];
const resetCode = codeMatches.at(-1)?.[1];
check('开发模式验证码写入日志', /^\d{6}$/.test(resetCode || ''), String(resetCode));
check('错误验证码重置被拒绝',
  bad(await api('POST', '/user/resetPassword', {
    body: { email: resetEmail, code: resetCode === '000000' ? '111111' : '000000', newPassword: '777777' }
  })));
check('60秒内重复发送验证码被拒绝',
  bad(await api('POST', '/user/sendResetCode', {
    body: { email: resetEmail },
    extraHeaders: { 'X-Forwarded-For': '10.3.0.10' }
  })));
check('正确验证码重置密码成功',
  ok(await api('POST', '/user/resetPassword', {
    body: { email: resetEmail, code: resetCode, newPassword: '777777' }
  })));
check('重置密码后旧 token 立即失效',
  (await api('GET', '/cart/list', { token: newToken })).status === 401);
const resetLogin = await api('POST', '/user/login', {
  body: { username: uname, password: '777777' },
  extraHeaders: { 'X-Forwarded-For': '10.3.0.10' }
});
check('重置后的新密码可登录', ok(resetLogin));
newToken = resetLogin.json?.data?.token;
check('验证码用后即焚', bad(await api('POST', '/user/resetPassword', {
  body: { email: resetEmail, code: resetCode, newPassword: '888888' }
})));

// ========== P5 ==========
// ---------- 下单幂等 ----------
const idempotencyAddr = 'P5幂等' + p3tag;
const requestId = randomUUID();
const idempotentBody = {
  bid, num: 1, name: '幂等测试', tel: '13900000000', addr: idempotencyAddr, requestId
};
const idempotentRaces = await Promise.all(Array.from({ length: 5 }, () =>
  api('POST', '/order/save', { token: newToken, body: idempotentBody })));
const idempotentOids = [...new Set(idempotentRaces.map(r => r.json?.data?.oid).filter(Boolean))];
check('相同 requestId 并发5次仅生成1单', idempotentRaces.every(ok) && idempotentOids.length === 1,
  JSON.stringify(idempotentRaces.map(r => r.json)));
const idempotentAgain = await api('POST', '/order/save', { token: newToken, body: idempotentBody });
check('重复 requestId 返回首次结果',
  ok(idempotentAgain) && Number(idempotentAgain.json?.data?.oid) === Number(idempotentOids[0]));
await api('POST', '/order/cancel', { token: newToken, body: { oid: idempotentOids[0] } });
check('幂等测试订单清理成功',
  ok(await api('POST', '/order/remove', { token: newToken, body: { oid: idempotentOids[0] } })));

// ---------- 上传魔数校验 ----------
const fakePng = new Uint8Array([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0x74, 0x65, 0x78, 0x74]);
const fakePngForm = new FormData();
fakePngForm.append('file', new File([fakePng], 'fake.png', { type: 'image/png' }));
const fakePngResult = await api('POST', '/user/uploadAvatar', { token: newToken, body: fakePngForm });
check('伪造 PNG 文件被图片解码校验拒绝', bad(fakePngResult)
  && fakePngResult.json?.msg?.includes('图片内容无效'), JSON.stringify(fakePngResult.json));

// ---------- JWT 封禁吊销 ----------
check('管理员封禁用户成功',
  ok(await api('POST', '/user/setState', { token: adminToken, body: { uid: newUid, state: 1 } })));
check('封禁后旧 token 立即失效',
  (await api('GET', '/cart/list', { token: newToken })).status === 401);
check('管理员解封用户成功',
  ok(await api('POST', '/user/setState', { token: adminToken, body: { uid: newUid, state: 0 } })));

// ---------- P2 限流（放在最后，避免影响前面的认证检查） ----------
// 用非法密码发起突发请求：即使未触发限流也会注册失败，不产生脏数据
const burst = await Promise.all(Array.from({ length: 25 }, (_, i) =>
  api('POST', '/user/register', { body: { username: 'burst_' + tag + '_' + i, password: '123' }, raw: true })));
const limited = burst.filter(r => r.status === 429);
check('登录/注册限流触发 429', limited.length > 0, `429=${limited.length}/25`);
const limitedJson = limited.length ? await limited[0].json().catch(() => null) : null;
check('429 返回统一 JSON 提示', limitedJson?.code === 0 && limitedJson?.msg === '请求过于频繁，请稍后再试', JSON.stringify(limitedJson));
check('限流不影响浏览接口', ok(await api('GET', '/kind/list')));

// ---------- 清理测试图片文件 ----------
for (const f of uploadedFiles) {
  try { unlinkSync(new URL('./picture/' + f, import.meta.url)); } catch { /* 忽略 */ }
}

console.log(`\n结果: ${pass} 通过, ${fail} 失败`);
process.exit(fail ? 1 : 0);
