package dev.rumble.cafe.service.product.impl;

import dev.rumble.cafe.model.entity.Product;
import dev.rumble.cafe.repository.ProductRepository;
import dev.rumble.cafe.service.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }
}
