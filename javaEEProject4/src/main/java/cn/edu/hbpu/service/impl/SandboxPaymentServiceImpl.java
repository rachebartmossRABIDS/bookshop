package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.PaymentMapper;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Payment;
import cn.edu.hbpu.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SandboxPaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;

    @Override
    public Payment pay(Order order, String channel) {
        if (order == null || order.getOid() == null) {
            throw new BusinessException("订单信息异常，无法支付");
        }
        Payment exists = paymentMapper.selectByOid(order.getOid());
        if (exists != null) {
            return exists;
        }
        Payment payment = new Payment();
        payment.setOid(order.getOid());
        payment.setChannel(channel == null || channel.trim().isEmpty() ? "sandbox" : channel.trim());
        payment.setAmount(order.getAmount());
        payment.setStatus("PAID");
        payment.setTradeNo("SB" + UUID.randomUUID().toString().replace("-", "").toUpperCase());
        payment.setTime(new Date());
        if (paymentMapper.insert(payment) != 1) {
            throw new BusinessException("支付记录写入失败");
        }
        return payment;
    }

    @Override
    public Payment getByOid(Integer oid) {
        return paymentMapper.selectByOid(oid);
    }

    @Override
    public void markRefunded(Integer oid) {
        paymentMapper.markRefunded(oid);
    }
}
