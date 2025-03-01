package com.xfef0.try_cache.service;

import com.xfef0.try_cache.entity.Product;
import com.xfef0.try_cache.exception.ResourceAlreadyExistsException;
import com.xfef0.try_cache.exception.ResourceNotFoundException;
import com.xfef0.try_cache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable("products")
    public List<Product> getAll() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(2));
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key="#productId")
    public Product getProductById(Long productId) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Product with id %d not found", productId))
                );
    }

    @CachePut(value = "products", key="#product.id")
    public Product addProduct(Product product) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
        Optional<Product> productInDB = productRepository.findByNameAndBrand(product.getName(), product.getBrand());
        if (productInDB.isPresent()) {
            throw new ResourceAlreadyExistsException("Already exists product with id=" + productInDB.get().getId());
        }
        return productRepository.save(product);
    }

    @CachePut(value = "products", key="#productId")
    public Product updateProduct(Product updatedProduct, Long productId) throws InterruptedException {
        Product product = getProductById(productId);
        product.setBrand(updatedProduct.getBrand());
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long productId) throws InterruptedException {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }
}
