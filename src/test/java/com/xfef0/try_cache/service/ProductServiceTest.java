package com.xfef0.try_cache.service;

import com.xfef0.try_cache.config.RedisConfig;
import com.xfef0.try_cache.entity.Product;
import com.xfef0.try_cache.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({ RedisConfig.class, ProductService.class })
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
class ProductServiceTest {

    private static final long ID = 123L;

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldReturnProductFromCache() throws InterruptedException {
        Product product = getProduct();
        given(productRepository.findById(ID))
                .willReturn(Optional.of(product));

        Product productCacheMiss = productService.getProductById(ID);
        Product productCacheHit = productService.getProductById(ID);

        compareProducts(productCacheMiss, product);
        compareProducts(productCacheHit, product);
        verify(productRepository, times(1)).findById(ID);
        compareProducts(productFromCache(), productCacheHit);
    }

    @Test
    void shouldFailToGetFromCacheWhenTTLExpires() throws InterruptedException {
        Product product = getProduct();
        given(productRepository.findById(ID))
                .willReturn(Optional.of(product));

        Product productCacheMiss = productService.getProductById(ID);
        compareProducts(productFromCache(), productCacheMiss);

        Thread.sleep(Duration.ofSeconds(20));

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(this::productFromCache);
    }

    private static Product getProduct() {
        return new Product("Xiaomi", "SU7", 29999.99);
    }

    private static void compareProducts(Product productCacheMiss, Product product) {
        assertThat(productCacheMiss.getBrand()).isEqualTo(product.getBrand());
        assertThat(productCacheMiss.getId()).isEqualTo(product.getId());
        assertThat(productCacheMiss.getName()).isEqualTo(product.getName());
        assertThat(productCacheMiss.getPrice()).isEqualTo(product.getPrice());
    }

    private Product productFromCache() {
        return (Product) cacheManager.getCache("products")
                .get(ID)
                .get();
    }
}