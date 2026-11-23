package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.CartMapper;
import cn.edu.hbpu.mapper.OrderRequestMapper;
import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Cart;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.service.OrderService;
import cn.edu.hbpu.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock private CartMapper cartMapper;
    @Mock private BookMapper bookMapper;
    @Mock private OrderService orderService;
    @Mock private OrderRequestMapper orderRequestMapper;
    @InjectMocks private CartServiceImpl service;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(7, "cart-user", 1));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void failedCheckoutKeepsCartAndDoesNotContinueWriting() {
        Cart first = cart(1, 1);
        Cart second = cart(2, 2);
        when(cartMapper.selectByUid(7)).thenReturn(Arrays.asList(first, second));
        when(bookMapper.getWithStock(1)).thenReturn(onSaleBook(1));
        when(bookMapper.getWithStock(2)).thenReturn(onSaleBook(2));
        when(orderService.save(any(Order.class)))
                .thenReturn(true)
                .thenThrow(new BusinessException("模拟第二条下单失败"));

        assertThrows(BusinessException.class,
                () -> service.checkout("张三", "13800000000", "测试地址", null));
        verify(cartMapper, never()).deleteByUid(anyInt());
        verify(orderService, times(2)).save(any(Order.class));
    }

    private Cart cart(int cid, int bid) {
        Cart cart = new Cart();
        cart.setCid(cid);
        cart.setUid(7);
        cart.setBid(bid);
        cart.setNum(1);
        return cart;
    }

    private Book onSaleBook(int bid) {
        Book book = new Book();
        book.setBid(bid);
        book.setName("测试书" + bid);
        book.setState(0);
        book.setInventory(10);
        return book;
    }
}
