package com.example.shopping.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.shopping.entity.Order;
import com.example.shopping.enumeration.PaymentMethod;

@JdbcTest
class JdbcOrderRepositoryTest {

	@Autowired
	JdbcTemplate jdbcTemplatete;
	
	JdbcOrderRepository jdbcOrderRepository;
	
	@BeforeEach
	void setUp() {
		jdbcOrderRepository = new JdbcOrderRepository(jdbcTemplatete);
	}
	
	@Test
	void insert_t_orderテーブルにデータが投入されること() {
		Order beforeOrder = new Order();
        beforeOrder.setId("o01");
        beforeOrder.setOrderDateTime(LocalDateTime.of(2023, 9, 3, 9, 8));
        beforeOrder.setBillingAmount(1);
        beforeOrder.setCustomerName("Nakamura");
        beforeOrder.setCustomerAddress("Tokyo");
        beforeOrder.setCustomerPhone("0123-4444-5555");
        beforeOrder.setCustomerEmailAddress("nakamura@jp.lucky.com");
        beforeOrder.setPaymentMethod(PaymentMethod.CONVENIENCE_STORE);
        jdbcOrderRepository.insert(beforeOrder);
        
        
        Order afterOrder = jdbcTemplatete.queryForObject("SELECT * FROM t_order WHERE id='o01'", new DataClassRowMapper<>(Order.class));
        assertThat(afterOrder.getId()).isEqualTo("o01");
        assertThat(afterOrder.getOrderDateTime()).isEqualTo(LocalDateTime.of(2023, 9, 3, 9, 8));
        assertThat(afterOrder.getBillingAmount()).isEqualTo(1);
        assertThat(afterOrder.getCustomerName()).isEqualTo("Nakamura");
        assertThat(afterOrder.getCustomerAddress()).isEqualTo("Tokyo");
        assertThat(afterOrder.getCustomerPhone()).isEqualTo("0123-4444-5555");
        assertThat(afterOrder.getCustomerEmailAddress()).isEqualTo("nakamura@jp.lucky.com");
        assertThat(afterOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CONVENIENCE_STORE);
	}

}
