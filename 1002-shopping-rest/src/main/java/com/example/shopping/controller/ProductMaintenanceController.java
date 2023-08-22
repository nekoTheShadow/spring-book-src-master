package com.example.shopping.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.entity.Product;
import com.example.shopping.service.ProductMaintenanceService;

@RestController
public class ProductMaintenanceController {
	private final ProductMaintenanceService productMaintenanceService;
	
	public ProductMaintenanceController(ProductMaintenanceService productMaintenanceService) {
		this.productMaintenanceService = productMaintenanceService;
	}
	
	@GetMapping("/product/all")
	public List<Product> getAllProducts() {
		return productMaintenanceService.findAll();
	}
	
	@GetMapping("/product/{id}")
	public Product getProductById(@PathVariable String id) {
		return productMaintenanceService.findById(id);
	}
}
