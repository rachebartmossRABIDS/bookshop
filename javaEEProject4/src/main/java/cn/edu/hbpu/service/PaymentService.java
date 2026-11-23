package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Payment;

/**
 * 支付渠道抽象。业务层只依赖该接口，便于替换为真实支付网关。
 */
public interface PaymentService {
    Payment pay(Order order, String channel);

    Payment getByOid(Integer oid);

    void markRefunded(Integer oid);
}
