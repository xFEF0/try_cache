package com.xfef0.try_cache.controller;

import com.xfef0.try_cache.entity.Product;
import com.xfef0.try_cache.exception.ResourceNotFoundException;
import com.xfef0.try_cache.response.ApiResponse;
import com.xfef0.try_cache.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        try {
            List<Product> products = productService.getAll();
            return ResponseEntity.ok(new ApiResponse("Products found: ", products));
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Product found: ", product));
        } catch (ResourceNotFoundException e) {
            return getExceptionResponseEntity(e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestBody Product product) {
        try {
            Product productAdded = productService.addProduct(product);
            return ResponseEntity.ok(new ApiResponse("Product added", productAdded));
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody Product product, @PathVariable Long productId) {
        try {
            Product updatedProduct = productService.updateProduct(product, productId);
            return ResponseEntity.ok(new ApiResponse("Product updated: ", updatedProduct));
        } catch (ResourceNotFoundException e) {
            return getExceptionResponseEntity(e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(new ApiResponse("Product with id=" + productId + " deleted", null));
        } catch (ResourceNotFoundException e) {
            return getExceptionResponseEntity(e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static ResponseEntity<ApiResponse> getExceptionResponseEntity(Exception e, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse(e.getMessage(), null));
    }
}
