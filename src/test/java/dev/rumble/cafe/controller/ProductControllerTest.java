package dev.rumble.cafe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rumble.cafe.model.dto.ProductInfoResDto;
import dev.rumble.cafe.model.entity.Product;
import dev.rumble.cafe.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Product> productList;

    @BeforeEach
    public void setup() {
        productList = Arrays.asList(
                new Product("Espresso", 3000, 10),
                new Product("Latte", 4000, 3),
                new Product("Cappuccino", 4500, 3)
        );
    }

    @Test
    @DisplayName("모든 제품 조회")
    public void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(productList);

        List<ProductInfoResDto> productDtos = productList.stream().map(product -> new ProductInfoResDto(
                product.getId(),
                product.getStockQuantity(),
                product.getName())).toList();

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDtos)));
    }
}
