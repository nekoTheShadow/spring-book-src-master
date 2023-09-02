package com.example.shopping.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.OrderItem;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("OrderControllerIntegrationTest.sql")
class OrderControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Test
    void test_order() throws Exception {
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

        OrderSession orderSession = new OrderSession();
        orderSession.setOrderInput(orderInput);
        orderSession.setCartInput(cartInput);
        
        MvcResult mvcResult = mockMvc.perform(post("/order/place-order").param("order", "").sessionAttr("scopedTarget.orderSession", orderSession))
        	.andExpect(redirectedUrl("/order/display-completion"))
        	.andReturn();
        Order resultOrder = (Order)mvcResult.getFlashMap().get("order");
        
        // INSERTされた値をテストする
        Order dbOrder = jdbcTemplate.queryForObject("SELECT * FROM t_order WHERE id = ?", new DataClassRowMapper<>(Order.class), resultOrder.getId());
        assertThat(dbOrder.getId()).isEqualTo(resultOrder.getId());
        assertThat(dbOrder.getOrderDateTime()).isNotNull();
        assertThat(dbOrder.getBillingAmount()).isEqualTo(1210);
        assertThat(dbOrder.getCustomerName()).isEqualTo("東京太郎");
        assertThat(dbOrder.getCustomerAddress()).isEqualTo("東京都");
        assertThat(dbOrder.getCustomerPhone()).isEqualTo("090-0000-0000");
        assertThat(dbOrder.getCustomerEmailAddress()).isEqualTo("taro@example.com");
        assertThat(dbOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);
        
        // INSERTされた値をテストする。
        List<OrderItem> dbOrderItems = jdbcTemplate.query("SELECT * FROM t_order_item WHERE order_id = ?", new DataClassRowMapper<>(OrderItem.class), resultOrder.getId());
        assertThat(dbOrderItems.get(0).getOrderId()).isEqualTo(resultOrder.getId());
        assertThat(dbOrderItems.get(0).getProductId()).isEqualTo("p01");
        assertThat(dbOrderItems.get(0).getPriceAtOrder()).isEqualTo(100);
        assertThat(dbOrderItems.get(0).getQuantity()).isEqualTo(3);
        assertThat(dbOrderItems.get(1).getOrderId()).isEqualTo(resultOrder.getId());
        assertThat(dbOrderItems.get(1).getProductId()).isEqualTo("p02");
        assertThat(dbOrderItems.get(1).getPriceAtOrder()).isEqualTo(200);
        assertThat(dbOrderItems.get(1).getQuantity()).isEqualTo(4);
        
        // UPDATEされた値をテストする。
        assertThat(jdbcTemplate.queryForObject("SELECT stock FROM t_product WHERE id = 'p01'", Integer.class)).isEqualTo(7);
        assertThat(jdbcTemplate.queryForObject("SELECT stock FROM t_product WHERE id = 'p02'", Integer.class)).isEqualTo(16);
    }
    
    @Test
    void test_order_fail() throws Exception { 
        OrderInput orderInput = new OrderInput();
        orderInput.setPaymentMethod(PaymentMethod.CONVENIENCE_STORE);

        CartInput cartInput = new CartInput();
        List<CartItemInput> cartItemInputs = new ArrayList<>();
        CartItemInput cartItemInput = new CartItemInput();
        cartItemInput.setProductId("p01");
        cartItemInput.setQuantity(1000);
        cartItemInput.setProductPrice(10);
        cartItemInputs.add(cartItemInput);
        cartInput.setCartItemInputs(cartItemInputs);

        OrderSession orderSession = new OrderSession();
        orderSession.setOrderInput(orderInput);
        orderSession.setCartInput(cartInput);
        
        mockMvc.perform(post("/order/place-order").param("order", "").sessionAttr("scopedTarget.orderSession", orderSession))
        	.andExpect(view().name("order/stockShortage"))
        	.andExpect(content().string(containsString("在庫不足")));
    }
}