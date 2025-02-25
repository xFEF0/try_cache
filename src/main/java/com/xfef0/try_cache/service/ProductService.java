package com.xfef0.try_cache.service;

import com.xfef0.try_cache.entity.Product;
import com.xfef0.try_cache.exception.ResourceNotFoundException;
import com.xfef0.try_cache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(2));
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Product with id %d not found", productId))
                );
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product updatedProduct, Long productId) throws InterruptedException {
        Product product = getProductById(productId);
        product.setBrand(updatedProduct.getBrand());
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) throws InterruptedException {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }
}
