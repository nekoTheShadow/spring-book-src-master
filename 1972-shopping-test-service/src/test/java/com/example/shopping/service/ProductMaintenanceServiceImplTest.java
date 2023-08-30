package com.example.shopping.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import com.example.shopping.entity.Product;
import com.example.shopping.input.ProductMaintenanceInput;
import com.example.shopping.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductMaintenanceServiceImplTest {
    @InjectMocks
    ProductMaintenanceServiceImpl productMaintenanceService;

    @Mock
    ProductRepository productRepository;

    @Test
    public void test_update() {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        doReturn(true).when(productRepository).update(productCaptor.capture());
        
        ProductMaintenanceInput productMaintenanceInput = new ProductMaintenanceInput();
        productMaintenanceInput.setId("p01");
        productMaintenanceInput.setName("pname01");
        productMaintenanceInput.setPrice(100);
        productMaintenanceInput.setStock(10);
        productMaintenanceService.update(productMaintenanceInput);
        
        assertThat(productCaptor.getValue().getId()).isEqualTo("p01");
        assertThat(productCaptor.getValue().getName()).isEqualTo("pname01");
        assertThat(productCaptor.getValue().getPrice()).isEqualTo(100);
        assertThat(productCaptor.getValue().getStock()).isEqualTo(10);
    }

    @Test
    public void test_update_更新に失敗() {
        doReturn(false).when(productRepository).update(any());
        ProductMaintenanceInput productMaintenanceInput = new ProductMaintenanceInput();

        assertThatThrownBy(() -> {
            productMaintenanceService.update(productMaintenanceInput);
        }).isInstanceOf(OptimisticLockingFailureException.class);
    }
    
    @Test
    public void test_findAll() {
    	doReturn(List.of(new Product(), new Product(), new Product())).when(productRepository).selectAll();
    	List<Product> products = productMaintenanceService.findAll();
    	assertThat(products.size()).isEqualTo(3);
    }
    
    public void test_findById() {
		Product beforeProduct = new Product();
		beforeProduct.setId("p02");
		beforeProduct.setName("ノート(改)");
		beforeProduct.setPrice(2000);
		beforeProduct.setStock(200);
		doReturn(beforeProduct).when(productRepository).selectById("p02");
		
		Product afterProduct = productMaintenanceService.findById("p02");
		assertThat(afterProduct.getId()).isEqualTo("p02");
		assertThat(afterProduct.getName()).isEqualTo("ノート(改)");
		assertThat(afterProduct.getPrice()).isEqualTo(2000);
		assertThat(afterProduct.getStock()).isEqualTo(200);
    }
}
