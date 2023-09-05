package com.example.shopping.ui;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.codeborne.selenide.Configuration;
import com.example.shopping.entity.Order;
import com.example.shopping.enumeration.PaymentMethod;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderUiTest {
    @Value("${local.server.port}")
    int randomPort;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        Configuration.baseUrl = "http://localhost:" + randomPort;
        Configuration.browser = "edge"; // Edgeにするとなぜか動く
    }

    @Test
    @Sql("OrderUiTest.sql")
    @Sql(value = "clear.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void test_注文() {
        open("/catalog/display-list");
        $("a[href*=\"productId=p01\"]").click();
        $("input[name=quantity]").setValue("2");
        $("input[value=カートに追加]").click();
        $("input[value=注文する]").click();
        $("input[name=name]").setValue("東京太郎");
        $("input[name=address]").setValue("東京都");
        $("input[name=phone]").setValue("090-0000-0000");
        $("input[name=emailAddress]").setValue("taro@example.com");
        $("input[name=paymentMethod]").selectRadio("CONVENIENCE_STORE");
        $("input[value=注文内容を確認]").click();
        $("input[value=注文を確定]").click();
        
        String orderId = $("div span").text();
        Order order = jdbcTemplate.queryForObject("SELECT * FROM t_order WHERE id=?", new DataClassRowMapper<>(Order.class), orderId);
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getBillingAmount()).isEqualTo(440);
        assertThat(order.getCustomerName()).isEqualTo("東京太郎");
        assertThat(order.getCustomerAddress()).isEqualTo("東京都");
        assertThat(order.getCustomerPhone()).isEqualTo("090-0000-0000");
        assertThat(order.getCustomerEmailAddress()).isEqualTo("taro@example.com");
        assertThat(order.getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);
    }
    
    @Test
    @Sql("OrderUiTest.sql")
    @Sql(value = "clear.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void test_注文フォーム入力エラー() {
        open("/catalog/display-list");
        $("a[href*=\"productId=p01\"]").click();
        $("input[name=quantity]").setValue("2");
        $("input[value=カートに追加]").click();
        $("input[value=注文する]").click();
        $("input[value=注文内容を確認]").click();
        $("td div").should(exactText("名前は必須です"));
    }
}
