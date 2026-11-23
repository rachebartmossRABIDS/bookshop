// 程序生成演示占位图：360 张图书封面 b1–b360.jpg（600×800）+ 40 张店铺图 s1–s40.jpg（800×400）
// 用法：npm i --no-save --no-package-lock @napi-rs/canvas mysql2 && node tools/gen-placeholder-images.mjs
// 连接可用 DB_HOST/DB_USER/DB_PASSWORD/DB_NAME 覆盖；按数据库中的 image/picture 文件名输出到 picture/
import { createCanvas, GlobalFonts } from '@napi-rs/canvas';
import mysql from 'mysql2/promise';
import { writeFileSync, mkdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = join(dirname(fileURLToPath(import.meta.url)), '..');
const OUT = join(ROOT, 'picture');
mkdirSync(OUT, { recursive: true });
for (const [f, alias] of [['msyh.ttc', 'YH'], ['msyhbd.ttc', 'YHB']]) {
  GlobalFonts.registerFromPath('C:/Windows/Fonts/' + f, alias);
}

const c = await mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '123456',
  database: process.env.DB_NAME || 'bookshop'
});
const [books] = await c.query('select bid,name,author,press,isbn,kid,image from book order by bid');
const [shops] = await c.query('select sid,name,score,kid,picture from shop order by sid');
await c.end();

const rng = seed => () => {
  seed |= 0; seed = seed + 0x6D2B79F5 | 0;
  let t = Math.imul(seed ^ seed >>> 15, 1 | seed);
  t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t;
  return ((t ^ t >>> 14) >>> 0) / 4294967296;
};
const KIND = { 1: '文学', 2: '计算机', 3: '历史', 4: '科学', 5: '艺术' };
// 五个板块各自的色彩家族：主色大场 + 高饱和点缀 + 纸色/墨色中性色
const PAL = {
  1: { bg: '#f3ecdd', bg2: '#e7dcc4', ink: '#2b2620', sub: '#6b5f4e', acc: '#c3272b', acc2: '#8a6d3b', line: '#c9b98f' },
  2: { bg: '#0d1b2a', bg2: '#12263a', ink: '#eaf6ff', sub: '#8fb3c7', acc: '#00e5ff', acc2: '#7cff5e', line: '#1d3a52' },
  3: { bg: '#e9dab4', bg2: '#d9c493', ink: '#33241a', sub: '#6d5233', acc: '#9c3b1e', acc2: '#a8763e', line: '#bfa87a' },
  4: { bg: '#0b1026', bg2: '#1b2a5e', ink: '#f2f6ff', sub: '#9fb0d8', acc: '#ffb400', acc2: '#4dd0e1', line: '#2c3a6e' },
  5: { bg: '#faf7f2', bg2: '#efe8dd', ink: '#191919', sub: '#6f6a63', acc: '#e64a19', acc2: '#00796b', line: '#d8d0c4' }
};
const ART_HUES = ['#e64a19', '#00796b', '#fbc02d', '#283593', '#c2185b', '#5d4037'];

const wrap = (ctx, text, max) => {
  const lines = []; let cur = '';
  for (const ch of text) {
    if (ctx.measureText(cur + ch).width > max && cur) { lines.push(cur); cur = ch; }
    else cur += ch;
  }
  if (cur) lines.push(cur);
  return lines;
};
const grain = (ctx, r, w, h, color, n = 420) => {
  ctx.fillStyle = color;
  for (let i = 0; i < n; i++) { ctx.globalAlpha = 0.03 + r() * 0.05; ctx.fillRect(r() * w, r() * h, 1.6, 1.6); }
  ctx.globalAlpha = 1;
};
const seal = (ctx, x, y, s, ch, color = '#c3272b') => {
  ctx.save(); ctx.translate(x + s / 2, y + s / 2); ctx.rotate(-0.05);
  ctx.fillStyle = color; ctx.fillRect(-s / 2, -s / 2, s, s);
  ctx.strokeStyle = 'rgba(255,255,255,.85)'; ctx.lineWidth = 3; ctx.strokeRect(-s / 2 + 5, -s / 2 + 5, s - 10, s - 10);
  ctx.fillStyle = '#fff'; ctx.font = `${s * 0.56}px YHB`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
  ctx.fillText(ch, 0, 2); ctx.restore();
};
const barcode = (ctx, x, y, isbn) => {
  ctx.fillStyle = '#fff'; ctx.fillRect(x, y, 168, 56);
  ctx.fillStyle = '#111';
  let cx = x + 8;
  for (const d of isbn) { const w = 1 + (Number(d) % 4); ctx.fillRect(cx, y + 6, w, 32); cx += w + 2; }
  ctx.font = '12px YH'; ctx.textAlign = 'center'; ctx.fillText('ISBN ' + isbn, x + 84, y + 50);
};
const stars = (ctx, x, y, size, score, color) => {
  for (let i = 0; i < 5; i++) {
    const cx = x + i * (size + 6) + size / 2, cy = y + size / 2;
    ctx.beginPath();
    for (let k = 0; k < 10; k++) {
      const rad = k % 2 ? size * 0.22 : size * 0.5;
      const a = -Math.PI / 2 + k * Math.PI / 5;
      ctx[k ? 'lineTo' : 'moveTo'](cx + rad * Math.cos(a), cy + rad * Math.sin(a));
    }
    ctx.closePath();
    ctx.fillStyle = i + 0.5 <= score ? color : 'rgba(128,128,128,.35)';
    ctx.fill();
  }
};

function cover(book) {
  const W = 600, H = 800;
  const cv = createCanvas(W, H); const ctx = cv.getContext('2d');
  const r = rng(book.bid * 7919 + 13);
  const p = PAL[book.kid];
  const variant = Math.floor(r() * 3);
  const g = ctx.createLinearGradient(0, 0, W * 0.4, H);
  g.addColorStop(0, p.bg); g.addColorStop(1, p.bg2);
  ctx.fillStyle = g; ctx.fillRect(0, 0, W, H);

  if (book.kid === 1) {
    ctx.strokeStyle = p.line; ctx.lineWidth = 2; ctx.strokeRect(26, 26, W - 52, H - 52);
    ctx.strokeRect(34, 34, W - 68, H - 68);
    grain(ctx, r, W, H, p.ink);
    if (variant === 0) {
      // 竖排书名，右起逐列
      ctx.fillStyle = p.ink; ctx.textBaseline = 'top'; ctx.textAlign = 'center';
      const size = book.name.length > 9 ? 46 : 56;
      ctx.font = `${size}px YHB`;
      const per = Math.floor((H - 300) / (size + 10));
      let x = W - 110;
      for (let i = 0; i < book.name.length; i += per) {
        const col = book.name.slice(i, i + per);
        [...col].forEach((ch, k) => ctx.fillText(ch, x, 110 + k * (size + 10)));
        x -= size + 34;
      }
      ctx.font = '26px YH'; ctx.fillStyle = p.sub;
      [...book.author].forEach((ch, k) => ctx.fillText(ch, x + 10, 130 + k * 32));
      seal(ctx, 60, H - 200, 64, KIND[1]);
    } else {
      ctx.strokeStyle = p.acc; ctx.lineWidth = 10; ctx.globalAlpha = .8;
      ctx.beginPath(); ctx.arc(W / 2, 300, 150 + variant * 20, 0.3, Math.PI * 2 - 0.2); ctx.stroke();
      ctx.globalAlpha = 1;
      ctx.fillStyle = p.ink; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
      ctx.font = '52px YHB';
      const lines = wrap(ctx, book.name, W - 160);
      lines.forEach((l, i) => ctx.fillText(l, W / 2, 260 + i * 66 - (lines.length - 1) * 33));
      ctx.font = '26px YH'; ctx.fillStyle = p.sub;
      ctx.fillText(book.author + ' 著', W / 2, 400);
      seal(ctx, W / 2 - 32, 450, 64, KIND[1]);
    }
    ctx.fillStyle = p.ink; ctx.font = '24px YH'; ctx.textAlign = 'left'; ctx.textBaseline = 'alphabetic';
    ctx.fillText(book.press, 60, H - 60);
    ctx.fillStyle = p.acc; ctx.fillRect(60, H - 100, 120, 4);
  }

  if (book.kid === 2) {
    ctx.strokeStyle = p.line; ctx.lineWidth = 1;
    for (let x = 0; x < W; x += 40) { ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, H); ctx.stroke(); }
    for (let y = 0; y < H; y += 40) { ctx.beginPath(); ctx.moveTo(0, y); ctx.lineTo(W, y); ctx.stroke(); }
    for (let i = 0; i < 7; i++) {
      ctx.strokeStyle = i % 2 ? p.acc : p.acc2; ctx.globalAlpha = .5; ctx.lineWidth = 2;
      let x = r() * W, y = r() * H; ctx.beginPath(); ctx.moveTo(x, y);
      for (let k = 0; k < 4; k++) { if (k % 2) x += (r() - .5) * 240; else y += (r() - .5) * 240; ctx.lineTo(x, y); }
      ctx.stroke(); ctx.globalAlpha = 1;
      ctx.fillStyle = p.acc; ctx.beginPath(); ctx.arc(x, y, 4, 0, 7); ctx.fill();
    }
    const glow = ctx.createRadialGradient(W * .8, H * .18, 10, W * .8, H * .18, 260);
    glow.addColorStop(0, 'rgba(0,229,255,.28)'); glow.addColorStop(1, 'rgba(0,229,255,0)');
    ctx.fillStyle = glow; ctx.fillRect(0, 0, W, H);
    ctx.fillStyle = p.acc2; ctx.font = '22px YH'; ctx.textAlign = 'left'; ctx.textBaseline = 'alphabetic';
    ctx.fillText('// ' + KIND[2] + ' · COMPUTER', 48, 84);
    ctx.fillStyle = p.ink; ctx.font = '46px YHB';
    const lines = wrap(ctx, book.name, W - 96);
    lines.forEach((l, i) => ctx.fillText(l, 48, 200 + i * 60));
    ctx.fillStyle = p.acc; ctx.font = '26px YH';
    ctx.fillText(book.author, 48, 200 + lines.length * 60 + 30);
    ctx.fillStyle = p.sub; ctx.font = '22px YH';
    ctx.fillText(book.press, 48, H - 96);
    ctx.fillStyle = p.acc; ctx.fillRect(48, H - 130, 90, 4);
    barcode(ctx, W - 216, H - 110, book.isbn);
  }

  if (book.kid === 3) {
    for (let x = 0, i = 0; x < W; x += 46, i++) {
      ctx.fillStyle = i % 2 ? p.bg2 : p.bg; ctx.fillRect(x, 0, 46, H);
      ctx.strokeStyle = p.line; ctx.lineWidth = 1.5;
      ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, H); ctx.stroke();
    }
    grain(ctx, r, W, H, p.ink, 300);
    ctx.fillStyle = 'rgba(51,36,26,.9)'; ctx.fillRect(0, 0, W, 64); ctx.fillRect(0, H - 64, W, 64);
    ctx.fillStyle = p.bg; ctx.font = '24px YH'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
    ctx.fillText(KIND[3] + ' · 史部典藏', W / 2, 34);
    ctx.fillStyle = p.ink; ctx.textBaseline = 'top';
    const size = book.name.length > 9 ? 44 : 54;
    ctx.font = `${size}px YHB`;
    const per = Math.floor((H - 260) / (size + 8));
    let x = W / 2 + ((Math.ceil(book.name.length / per) - 1) * (size + 26)) / 2;
    for (let i = 0; i < book.name.length; i += per) {
      [...book.name.slice(i, i + per)].forEach((ch, k) => ctx.fillText(ch, x, 120 + k * (size + 8)));
      x -= size + 26;
    }
    ctx.font = '24px YH'; ctx.fillStyle = p.sub;
    [...book.author].forEach((ch, k) => ctx.fillText(ch, x + 6, 140 + k * 30));
    seal(ctx, W - 120, H - 190, 60, KIND[3], p.acc);
    ctx.fillStyle = p.bg; ctx.font = '22px YH'; ctx.textBaseline = 'middle';
    ctx.fillText(book.press, W / 2, H - 32);
  }

  if (book.kid === 4) {
    for (let i = 0; i < 150; i++) {
      ctx.fillStyle = r() > .8 ? p.acc : '#fff'; ctx.globalAlpha = .25 + r() * .7;
      const s = r() * 2.2 + .6; ctx.fillRect(r() * W, r() * H, s, s);
    }
    ctx.globalAlpha = 1;
    const px = W * (.25 + r() * .5), py = H * (.2 + r() * .25), pr = 60 + r() * 50;
    const pg = ctx.createRadialGradient(px - pr * .4, py - pr * .4, pr * .1, px, py, pr);
    pg.addColorStop(0, p.acc); pg.addColorStop(1, '#7a4d00');
    ctx.fillStyle = pg; ctx.beginPath(); ctx.arc(px, py, pr, 0, 7); ctx.fill();
    ctx.strokeStyle = p.acc2; ctx.lineWidth = 2; ctx.globalAlpha = .7;
    for (let i = 0; i < 3; i++) {
      ctx.save(); ctx.translate(px, py); ctx.rotate(-.4 + i * .35);
      ctx.beginPath(); ctx.ellipse(0, 0, pr + 34 + i * 26, (pr + 34 + i * 26) * .32, 0, 0, 7); ctx.stroke(); ctx.restore();
    }
    ctx.globalAlpha = 1;
    ctx.fillStyle = p.acc2; ctx.font = '22px YH'; ctx.textAlign = 'left'; ctx.textBaseline = 'alphabetic';
    ctx.fillText('SCIENCE · ' + KIND[4], 48, H - 250);
    ctx.fillStyle = p.ink; ctx.font = '46px YHB';
    const lines = wrap(ctx, book.name, W - 96);
    lines.forEach((l, i) => ctx.fillText(l, 48, H - 190 + i * 58));
    ctx.fillStyle = p.acc; ctx.font = '26px YH';
    ctx.fillText(book.author, 48, H - 190 + lines.length * 58 + 26);
    ctx.fillStyle = p.sub; ctx.font = '22px YH'; ctx.fillText(book.press, 48, H - 48);
  }

  if (book.kid === 5) {
    const h1 = ART_HUES[book.bid % 6], h2 = ART_HUES[(book.bid + 2) % 6], h3 = ART_HUES[(book.bid + 4) % 6];
    if (variant === 0) {
      ctx.fillStyle = h1; ctx.beginPath(); ctx.arc(W * .68, H * .3, 170, 0, 7); ctx.fill();
      ctx.fillStyle = h2; ctx.fillRect(60, H * .42, 220, 220);
      ctx.fillStyle = h3; ctx.beginPath(); ctx.moveTo(60, H * .42); ctx.arc(60, H * .42, 220, 0, Math.PI / 2); ctx.closePath(); ctx.fill();
    } else if (variant === 1) {
      ctx.fillStyle = h1; ctx.fillRect(0, 0, W, H * .46);
      ctx.fillStyle = h2; ctx.beginPath(); ctx.arc(W / 2, H * .46, 150, 0, Math.PI); ctx.fill();
      ctx.fillStyle = h3; ctx.fillRect(W * .72, H * .12, 60, H * .6);
    } else {
      ctx.fillStyle = h1; ctx.beginPath(); ctx.moveTo(0, H * .5); ctx.lineTo(W * .5, 60); ctx.lineTo(W, H * .5); ctx.closePath(); ctx.fill();
      ctx.fillStyle = h2; ctx.beginPath(); ctx.arc(W * .3, H * .62, 110, 0, 7); ctx.fill();
      ctx.fillStyle = h3; ctx.fillRect(W * .55, H * .55, 180, 60);
    }
    grain(ctx, r, W, H, '#000', 260);
    ctx.fillStyle = p.ink; ctx.font = '22px YH'; ctx.textAlign = 'left'; ctx.textBaseline = 'alphabetic';
    ctx.fillText('ART · ' + KIND[5], 48, H - 220);
    ctx.font = '44px YHB';
    const lines = wrap(ctx, book.name, W - 96);
    lines.forEach((l, i) => ctx.fillText(l, 48, H - 160 + i * 54));
    ctx.fillStyle = p.sub; ctx.font = '24px YH';
    ctx.fillText(book.author + ' · ' + book.press, 48, H - 160 + lines.length * 54 + 24);
    ctx.fillStyle = h1; ctx.fillRect(48, H - 250, 70, 6);
  }
  return cv.encode('jpeg', 86);
}

function banner(shop) {
  const W = 800, H = 400;
  const cv = createCanvas(W, H); const ctx = cv.getContext('2d');
  const r = rng(shop.sid * 104729 + 7);
  const p = PAL[shop.kid];
  const g = ctx.createLinearGradient(0, 0, W, H);
  g.addColorStop(0, p.bg); g.addColorStop(.55, p.bg2); g.addColorStop(1, p.bg);
  ctx.fillStyle = g; ctx.fillRect(0, 0, W, H);
  if (shop.kid === 2 || shop.kid === 4) {
    for (let i = 0; i < 90; i++) {
      ctx.fillStyle = i % 3 ? '#fff' : p.acc; ctx.globalAlpha = .2 + r() * .6;
      ctx.fillRect(r() * W, r() * H, 1.8, 1.8);
    }
    ctx.globalAlpha = .5; ctx.strokeStyle = p.acc; ctx.lineWidth = 1.5;
    for (let i = 0; i < 4; i++) {
      ctx.beginPath(); ctx.ellipse(W * .82, H * .3, 60 + i * 34, (60 + i * 34) * .4, -.3, 0, 7); ctx.stroke();
    }
    ctx.globalAlpha = 1;
  } else if (shop.kid === 3) {
    for (let x = 0, i = 0; x < W; x += 40, i++) {
      ctx.fillStyle = i % 2 ? 'rgba(0,0,0,.05)' : 'rgba(255,255,255,.06)'; ctx.fillRect(x, 0, 40, H);
    }
  } else if (shop.kid === 5) {
    const h1 = ART_HUES[shop.sid % 6], h2 = ART_HUES[(shop.sid + 3) % 6];
    ctx.globalAlpha = .85;
    ctx.fillStyle = h1; ctx.beginPath(); ctx.arc(W * .12, H * .85, 120, 0, 7); ctx.fill();
    ctx.fillStyle = h2; ctx.beginPath(); ctx.arc(W * .9, H * .12, 90, 0, 7); ctx.fill();
    ctx.globalAlpha = 1;
  } else {
    ctx.strokeStyle = p.line; ctx.lineWidth = 2; ctx.strokeRect(18, 18, W - 36, H - 36);
    grain(ctx, r, W, H, p.ink, 260);
  }
  const dark = shop.kid === 2 || shop.kid === 4;
  ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
  ctx.fillStyle = dark ? p.sub : p.sub; ctx.font = '22px YH';
  ctx.fillText('BOOKSHOP · ' + KIND[shop.kid], W / 2, 92);
  ctx.fillStyle = dark ? p.ink : p.ink; ctx.font = '58px YHB';
  ctx.fillText(shop.name, W / 2, H / 2 - 8);
  const sw = 5 * 26 + 4 * 6;
  stars(ctx, W / 2 - sw / 2 - 30, H / 2 + 44, 26, Number(shop.score), p.acc === '#c3272b' ? '#e6a23c' : p.acc);
  ctx.fillStyle = dark ? p.acc : p.acc; ctx.font = '26px YHB'; ctx.textAlign = 'left';
  ctx.fillText(shop.score, W / 2 + sw / 2 - 18, H / 2 + 58);
  ctx.fillStyle = dark ? p.acc2 : p.acc; ctx.fillRect(W / 2 - 60, H - 74, 120, 4);
  if (shop.kid === 1 || shop.kid === 3) seal(ctx, 36, 36, 56, KIND[shop.kid], p.acc);
  return cv.encode('jpeg', 86);
}

let n = 0;
for (const b of books) { writeFileSync(join(OUT, b.image), await cover(b)); n++; }
for (const s of shops) { writeFileSync(join(OUT, s.picture), await banner(s)); n++; }
console.log('生成占位图:', n, '张 →', OUT);
