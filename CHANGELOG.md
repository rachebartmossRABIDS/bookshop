# Changelog

## [1.0.0] - 2026-11-23

### Added

- 沙箱支付抽象、支付记录、退款及库存/销量补偿。
- 收货地址簿、订单详情时间线、物流信息和站内通知。
- 评论评分、点赞和商家回复。
- 搜索历史、30 天热词和 Redis 缓存降级。
- 邮箱验证码找回密码。
- 管理看板、管理操作日志和评论回复入口。
- JUnit 5 + Mockito 单元测试、GitHub Actions CI。
- Docker Compose、Nginx、TLS 示例和完整 OpenAPI 文档。
- JWT `token_version` 吊销、Redis Lua 分布式限流、图片内容校验和下单幂等。

### Security

- 改密、重置密码和封禁会立即使旧 token 失效。
- 上传文件增加魔数、扩展名一致性和 ImageIO 可读性验证。
- 敏感配置支持环境变量覆盖，生产环境使用独立示例文件。

### Verified

- `mvn test` 通过。
- `npm run build` 通过。
- `node smoke-test.mjs`：228 通过，0 失败。
