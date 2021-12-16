package com.example.demo.data.repository;

import com.example.demo.data.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Mono<Product> findBySku(String sku);
    Mono<Boolean> deleteBySku(String sku);
}
