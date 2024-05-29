package dev.rumble.cafe.config;

import dev.rumble.cafe.model.entity.Product;
import dev.rumble.cafe.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        productRepository.save(new Product("Espresso", 3000, 10));
        productRepository.save(new Product("Latte", 4000, 3));
        productRepository.save(new Product("Cappuccino", 4500, 3));
        productRepository.save(new Product("Americano", 2500, 5));
        productRepository.save(new Product("Mocha", 5000, 10));
    }
}
