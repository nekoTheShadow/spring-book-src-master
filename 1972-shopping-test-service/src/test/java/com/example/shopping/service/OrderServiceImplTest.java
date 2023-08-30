package com.example.shopping.service;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.OrderItem;
import com.example.shopping.entity.Product;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.exception.StockShortageException;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;
import com.example.shopping.repository.OrderItemRepository;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    ProductRepository productRepository;
    
    
    @Test
    void test_placeOrder() {
    	ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    	ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    	ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);
    	doNothing().when(orderRepository).insert(orderCaptor.capture());
    	doReturn(true).when(productRepository).update(productCaptor.capture());
    	doNothing().when(orderItemRepository).insert(orderItemCaptor.capture());
        
        Product product1 = new Product();
        product1.setId("p01");
        product1.setStock(10);
        Product product2 = new Product();
        product2.setId("p02");
        product2.setStock(5);
        doReturn(product1).when(productRepository).selectById("p01");
        doReturn(product2).when(productRepository).selectById("p02");
                
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
        
        assertThat(order.getBillingAmount()).isEqualTo(1210);
        assertThat(order.getCustomerName()).isEqualTo("東京太郎");
        assertThat(order.getCustomerAddress()).isEqualTo("東京都");
        assertThat(order.getCustomerPhone()).isEqualTo("090-0000-0000");
        assertThat(order.getCustomerEmailAddress()).isEqualTo("taro@example.com");
        assertThat(order.getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);

        verify(orderRepository).insert(any());
        verify(productRepository, times(2)).update(any());
        verify(orderItemRepository, times(2)).insert(any());
        
        // updateされた値を検査する
        assertThat(productCaptor.getAllValues().get(0).getStock()).isEqualTo(7);
        assertThat(productCaptor.getAllValues().get(1).getStock()).isEqualTo(1);
        
        // insertされた値を検査する。
        assertThat(orderCaptor.getValue().getId()).isNotNull();
        assertThat(orderCaptor.getValue().getOrderDateTime()).isNotNull();
        assertThat(orderCaptor.getValue().getBillingAmount()).isEqualTo(1210);
        assertThat(orderCaptor.getValue().getCustomerName()).isEqualTo("東京太郎");
        assertThat(orderCaptor.getValue().getCustomerAddress()).isEqualTo("東京都");
        assertThat(orderCaptor.getValue().getCustomerPhone()).isEqualTo("090-0000-0000");
        assertThat(orderCaptor.getValue().getCustomerEmailAddress()).isEqualTo("taro@example.com");
        assertThat(orderCaptor.getValue().getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);
        
        // insertされた値を検査する。
        assertThat(orderItemCaptor.getAllValues().get(0).getOrderId()).isEqualTo(orderCaptor.getAllValues().get(0).getId());
        assertThat(orderItemCaptor.getAllValues().get(0).getProductId()).isEqualTo("p01");
        assertThat(orderItemCaptor.getAllValues().get(0).getPriceAtOrder()).isEqualTo(100);
        assertThat(orderItemCaptor.getAllValues().get(0).getQuantity()).isEqualTo(3);
        
        assertThat(orderItemCaptor.getAllValues().get(1).getOrderId()).isEqualTo(orderCaptor.getAllValues().get(0).getId());
        assertThat(orderItemCaptor.getAllValues().get(1).getProductId()).isEqualTo("p02");
        assertThat(orderItemCaptor.getAllValues().get(1).getPriceAtOrder()).isEqualTo(200);
        assertThat(orderItemCaptor.getAllValues().get(1).getQuantity()).isEqualTo(4);
    }
    
    @Test
    public void test_placeOrder_在庫が足りない() {
        Product product = new Product();
        product.setStock(2);
        doReturn(product).when(productRepository).selectById("p01");

        OrderInput orderInput = new OrderInput();
        List<CartItemInput> cartItemInputs = new ArrayList<>();
        CartItemInput cartItemInput = new CartItemInput();
        cartItemInput.setProductId("p01");
        cartItemInput.setProductPrice(100);
        cartItemInput.setQuantity(3);
        cartItemInputs.add(cartItemInput);
        CartInput cartInput = new CartInput();
        cartInput.setCartItemInputs(cartItemInputs);
        assertThatThrownBy(() -> {
            orderService.placeOrder(orderInput, cartInput);
        }).isInstanceOf(StockShortageException.class);
    }
}
