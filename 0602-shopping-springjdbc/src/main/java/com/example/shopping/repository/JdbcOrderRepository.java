package com.example.shopping.repository;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.shopping.entity.Order;

@Repository
public class JdbcOrderRepository implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Order order) {
        jdbcTemplate.update("INSERT INTO t_order values (?, ?, ?, ?, ?, ?, ?, ?)",
                order.getId(),
                order.getOrderDateTime(),
                order.getBillingAmount(),
                order.getCustomerName(),
                order.getCustomerAddress(),
                order.getCustomerPhone(),
                order.getCustomerEmailAddress(),
                order.getPaymentMethod().toString());
    }

	@Override
	public Order findById(String id) {
		return jdbcTemplate.queryForObject("SELECT * FROM t_order WHERE id=?", new DataClassRowMapper<>(Order.class), id);
		
	}
}
