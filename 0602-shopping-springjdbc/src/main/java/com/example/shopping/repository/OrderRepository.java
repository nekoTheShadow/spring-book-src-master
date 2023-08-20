package com.example.shopping.repository;

import com.example.shopping.entity.Order;

public interface OrderRepository {
    void insert(Order order);
    Order findById(String id);
}
