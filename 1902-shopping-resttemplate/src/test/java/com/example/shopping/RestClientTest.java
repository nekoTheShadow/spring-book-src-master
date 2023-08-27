package com.example.shopping;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.example.shopping.entity.Product;
import com.example.shopping.input.ProductMaintenanceInput;


public class RestClientTest {
    @Test
    public void test() {
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.setErrorHandler(new NotFoundErrorHandler());
    	
    	// 登録
    	ProductMaintenanceInput productMaintenanceInput1 = new ProductMaintenanceInput();
    	productMaintenanceInput1.setName("たこ焼き");
    	productMaintenanceInput1.setPrice(10);
    	productMaintenanceInput1.setStock(20);
    	URI uri = restTemplate.postForLocation("http://localhost:8080/api/products", productMaintenanceInput1);
    	System.out.printf("uri=%s%n", uri);
    	
    	// 参照(登録後)
    	Product product1 = restTemplate.getForObject(uri, Product.class);
    	System.out.printf("id=%s, name=%s, price=%d, stock=%d%n", product1.getId(), product1.getName(), product1.getPrice(), product1.getStock());
    	
    	// 更新
    	ProductMaintenanceInput productMaintenanceInput2 = new ProductMaintenanceInput();
    	productMaintenanceInput2.setId(product1.getId());
    	productMaintenanceInput2.setName("たこ焼き - 改");
    	productMaintenanceInput2.setPrice(30);
    	productMaintenanceInput2.setStock(40);
    	restTemplate.put("http://localhost:8080/api/products/{id}", productMaintenanceInput2, product1.getId());
    	
    	// 参照(更新後)
    	Product product2 = restTemplate.getForObject(uri, Product.class);
    	System.out.printf("id=%s, name=%s, price=%d, stock=%d%n", product2.getId(), product2.getName(), product2.getPrice(), product2.getStock());
    	
    	// 削除
    	restTemplate.delete(uri);
    	
    	// 参照(削除後)
    	restTemplate.getForEntity(uri, Product.class);
    }
    
    private static class NotFoundErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return response.getStatusCode().value() == 404;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			System.out.printf("statusCode=%s%n", response.getStatusCode());
		}
    }
}
