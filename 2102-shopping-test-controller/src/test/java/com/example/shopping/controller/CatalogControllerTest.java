package com.example.shopping.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shopping.entity.Product;
import com.example.shopping.service.CatalogService;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

	@Autowired
	MockMvc mockMvc;
	
    @MockBean
    CatalogService catalogService;

	
	@Test
	void test_displayList() throws Exception {
		Product product1 = new Product();
		Product product2 = new Product();
		Product product3 = new Product();
		product1.setName("商品01");
		product2.setName("商品02");
		product3.setName("商品03");
		doReturn(List.of(product1, product2, product3)).when(catalogService).findAll();
		
		mockMvc.perform(get("/catalog/display-list"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog/productList"))
			.andExpect(content().string(containsString("商品01")))
			.andExpect(content().string(containsString("商品02")))
			.andExpect(content().string(containsString("商品03")));
	}
	
	@Test
	void test_displayDetails() throws Exception {
		Product product = new Product();
		product.setName("商品01");
		doReturn(product).when(catalogService).findById("01");
		
		mockMvc.perform(get("/catalog/display-details").param("productId", "01"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog/productDetails"))
			.andExpect(content().string(containsString("商品01")));
		;
	}

}
