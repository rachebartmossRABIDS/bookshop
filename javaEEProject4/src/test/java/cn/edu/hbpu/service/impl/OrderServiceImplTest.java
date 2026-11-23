package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.OrderMapper;
import cn.edu.hbpu.mapper.OrderRequestMapper;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Payment;
import cn.edu.hbpu.service.CacheService;
import cn.edu.hbpu.service.NotificationService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.service.PaymentService;
import cn.edu.hbpu.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock private OrderMapper orderMapper;
    @Mock private BookMapper bookMapper;
    @Mock private UserMapper userMapper;
    @Mock private PaymentService paymentService;
    @Mock private NotificationService notificationService;
    @Mock private OperationLogService operationLogService;
    @Mock private OrderRequestMapper orderRequestMapper;
    @Mock private CacheService cacheService;
    @InjectMocks private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(1, "tester", 1));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void paymentRejectsInvalidStateWithoutSideEffects() {
        Order order = order(10, 1, Order.STATE_PAID);
        when(orderMapper.selectByPrimaryKey(10L)).thenReturn(order);
        when(orderMapper.updateState(10, Order.STATE_UNPAID, Order.STATE_PAID)).thenReturn(0);

        assertThrows(BusinessException.class, () -> service.pay(10, "sandbox"));
        verify(bookMapper, never()).addVolume(anyInt(), anyInt());
        verifyNoInteractions(paymentService, notificationService);
    }

    @Test
    void paymentWritesRecordAndSalesOnce() {
        Order order = order(11, 1, Order.STATE_UNPAID);
        Payment payment = new Payment();
        payment.setOid(11);
        when(orderMapper.selectByPrimaryKey(11L)).thenReturn(order);
        when(orderMapper.updateState(11, Order.STATE_UNPAID, Order.STATE_PAID)).thenReturn(1);
        when(paymentService.pay(order, "sandbox")).thenReturn(payment);

        assertSame(payment, service.pay(11, "sandbox"));
        verify(bookMapper).addVolume(order.getBid(), order.getNum());
        verify(notificationService).send(eq(1), eq("付款成功"), anyString());
    }

    @Test
    void shippedOrderCannotRefundOrCancel() {
        Order order = order(12, 1, Order.STATE_SHIPPED);
        when(orderMapper.selectByPrimaryKey(12L)).thenReturn(order);
        when(orderMapper.updateState(12, Order.STATE_PAID, Order.STATE_REFUNDED)).thenReturn(0);

        assertThrows(BusinessException.class, () -> service.refund(12));
        assertThrows(BusinessException.class, () -> service.cancel(12));
        verify(bookMapper, never()).restoreInventory(anyInt(), anyInt());
        verify(bookMapper, never()).subtractVolume(anyInt(), anyInt());
        verify(paymentService, never()).markRefunded(anyInt());
    }

    private Order order(int oid, int uid, String state) {
        Order order = new Order();
        order.setOid(oid);
        order.setUid(uid);
        order.setBid(1);
        order.setNum(2);
        order.setState(state);
        order.setAmount(new java.math.BigDecimal("70.00"));
        return order;
    }
}
