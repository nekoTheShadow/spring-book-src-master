package com.example.shopping.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.example.shopping.entity.Product;

@JdbcTest
@Sql("JdbcProductRepositoryTest.sql")
class JdbcProductRepositoryTest {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	JdbcProductRepository jdbcProductRepository;


	@BeforeEach
	void setUp() {
		jdbcProductRepository =  new JdbcProductRepository(jdbcTemplate);
	}
	
	@Test
	void selectAll_Productテーブルのデータがすべて取得できること() {
		List<Product> products = jdbcProductRepository.selectAll();
		assertThat(products.size()).isEqualTo(5);
		assertThat(products).anySatisfy(product -> {
			assertThat(product.getId()).isEqualTo("p01");
			assertThat(product.getName()).isEqualTo("消しゴム");
			assertThat(product.getPrice()).isEqualTo(100);
			assertThat(product.getStock()).isEqualTo(10);
		});
		assertThat(products).anySatisfy(product -> {
			assertThat(product.getId()).isEqualTo("p02");
			assertThat(product.getName()).isEqualTo("ノート");
			assertThat(product.getPrice()).isEqualTo(200);
			assertThat(product.getStock()).isEqualTo(20);
		});
		assertThat(products).anySatisfy(product -> {
			assertThat(product.getId()).isEqualTo("p03");
			assertThat(product.getName()).isEqualTo("pname03");
			assertThat(product.getPrice()).isEqualTo(300);
			assertThat(product.getStock()).isEqualTo(30);
		});
		assertThat(products).anySatisfy(product -> {
			assertThat(product.getId()).isEqualTo("p04");
			assertThat(product.getName()).isEqualTo("pname04");
			assertThat(product.getPrice()).isEqualTo(400);
			assertThat(product.getStock()).isEqualTo(40);
		});
		assertThat(products).anySatisfy(product -> {
			assertThat(product.getId()).isEqualTo("p05");
			assertThat(product.getName()).isEqualTo("pname05");
			assertThat(product.getPrice()).isEqualTo(500);
			assertThat(product.getStock()).isEqualTo(50);
		});
	}
	
	@Test
	void selectById_IDに対応するデータが取得できること() {
		Product product = jdbcProductRepository.selectById("p02");
		assertThat(product.getId()).isEqualTo("p02");
		assertThat(product.getName()).isEqualTo("ノート");
		assertThat(product.getPrice()).isEqualTo(200);
		assertThat(product.getStock()).isEqualTo(20);
	}
	
	@Test
	void update_IDに対応したデータが更新されること() {
		Product beforeProduct = new Product();
		beforeProduct.setId("p02");
		beforeProduct.setName("ノート(改)");
		beforeProduct.setPrice(2000);
		beforeProduct.setStock(200);
		
		assertTrue(jdbcProductRepository.update(beforeProduct));
		
		Product afterProduct = jdbcTemplate.queryForObject("SELECT * FROM t_product WHERE id='p02'", new DataClassRowMapper<>(Product.class));
		assertThat(afterProduct.getId()).isEqualTo("p02");
		assertThat(afterProduct.getName()).isEqualTo("ノート(改)");
		assertThat(afterProduct.getPrice()).isEqualTo(2000);
		assertThat(afterProduct.getStock()).isEqualTo(200);
	}

}
