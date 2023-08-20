package com.example.shopping;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.example.shopping.entity.Order;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.service.OrderService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@Configuration
@ComponentScan
public class ShoppingApplication {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder()
                .addScripts("schema.sql", "data.sql")
                .setType(EmbeddedDatabaseType.H2).build();
        return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    	return new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ShoppingApplication.class);
        // JdbcTemplateがSQLをログ出力してくれるように設定
        ((Logger) LoggerFactory.getLogger(JdbcTemplate.class.getName())).setLevel(Level.DEBUG);
        OrderService orderService = context.getBean(OrderService.class);

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

        System.out.println("注文確定処理が完了しました。注文ID=" + order.getId());
        
        // オプション:
        // JdbcOrderRepositoryクラスに、IDで検索してOrderオブジェクトを取得するメソッドを追加しましょう。
        // 追加できたら、TrainingApplicationクラスのmainメソッド内で注文確定が完了した後に、
        // JdbcOrderRepositoryクラスのBeanをDIコンテナから取得して、
        // 追加したメソッドを使って登録された注文データが取得できるか確認しましょう。
        OrderRepository orderRepository = context.getBean(OrderRepository.class);
        Order confirmedOrder = orderRepository.findById(order.getId());
        System.out.println("id                     : " + confirmedOrder.getId());
        System.out.println("order_date_time        : " + confirmedOrder.getOrderDateTime());
        System.out.println("billing_amount         : " + confirmedOrder.getBillingAmount());
        System.out.println("customer_name          : " + confirmedOrder.getCustomerName());
        System.out.println("customer_address       : " + confirmedOrder.getCustomerAddress());
        System.out.println("customer_phone         : " + confirmedOrder.getCustomerPhone());
        System.out.println("customer_email_address : " + confirmedOrder.getCustomerEmailAddress());
        System.out.println("payment_method         : " + confirmedOrder.getPaymentMethod());
    }
}

