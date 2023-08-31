package com.example.shopping.service;


import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.OrderItem;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.exception.StockShortageException;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
@Sql("OrderServiceTest.sql")
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void test_placeOrder() {
        OrderInput orderInput = new OrderInput();
        orderInput.setName("東京太郎");
        orderInput.setAddress("東京都");
        orderInput.setPhone("090-0000-0000");
        orderInput.setEmailAddress("taro@example.com");
        orderInput.setPaymentMethod(PaymentMethod.CONVENIENCE_STORE);

        List<CartItemInput> cartItemInputs = new ArrayList<>();

        CartItemInput keshigom = new CartItemInput();
        keshigom.setProductId("p01");
        keshigom.setProductName("消しゴム");
        keshigom.setProductPrice(100);
        keshigom.setQuantity(3);
        cartItemInputs.add(keshigom);

        CartItemInput note = new CartItemInput();
        note.setProductId("p02");
        note.setProductName("ノート");
        note.setProductPrice(200);
        note.setQuantity(4);
        cartItemInputs.add(note);

        CartInput cartInput = new CartInput();
        cartInput.setCartItemInputs(cartItemInputs);

        Order order = orderService.placeOrder(orderInput, cartInput);
        
        // a.発行された注文IDを使って注文データを検索し、取得した注文データの顧客名や住所が期待通りになっている
        Order dbOrder = jdbcTemplate.queryForObject("SELECT * FROM t_order WHERE id = ?", new DataClassRowMapper<>(Order.class), order.getId());
        assertThat(dbOrder.getBillingAmount()).isEqualTo(1210);
        assertThat(dbOrder.getCustomerName()).isEqualTo("東京太郎");
        assertThat(dbOrder.getCustomerAddress()).isEqualTo("東京都");
        assertThat(dbOrder.getCustomerPhone()).isEqualTo("090-0000-0000");
        assertThat(dbOrder.getCustomerEmailAddress()).isEqualTo("taro@example.com");
        assertThat(dbOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);
        
        // b.発行された注文IDに紐づく注文明細データの件数が期待通りになっている
        List<OrderItem> dbOrderItems = jdbcTemplate.query("SELECT * FROM t_order_item WHERE order_id = ?", new DataClassRowMapper<>(OrderItem.class), order.getId());
        assertThat(dbOrderItems.size()).isEqualTo(2);
        
        // c.注文した商品の在庫数が期待通りに変わっている
        assertThat(jdbcTemplate.queryForObject("SELECT stock FROM t_product WHERE id = 'p01'", Integer.class)).isEqualTo(7);
        assertThat(jdbcTemplate.queryForObject("SELECT stock FROM t_product WHERE id = 'p02'", Integer.class)).isEqualTo(16);
    }
    
    @Test
    void test_placeOrder_fail() {
        OrderInput orderInput = new OrderInput();
        orderInput.setName("東京太郎");
        orderInput.setAddress("東京都");
        orderInput.setPhone("090-0000-0000");
        orderInput.setEmailAddress("taro@example.com");
        orderInput.setPaymentMethod(PaymentMethod.CONVENIENCE_STORE);

        List<CartItemInput> cartItemInputs = new ArrayList<>();
        CartItemInput cartItemInput = new CartItemInput();
        cartItemInput.setProductId("p01");
        cartItemInput.setProductName("消しゴム");
        cartItemInput.setProductPrice(100);
        cartItemInput.setQuantity(300);
        cartItemInputs.add(cartItemInput);

        CartInput cartInput = new CartInput();
        cartInput.setCartItemInputs(cartItemInputs);

        assertThatThrownBy(() -> orderService.placeOrder(orderInput, cartInput)).isInstanceOf(StockShortageException.class);
    }
}
