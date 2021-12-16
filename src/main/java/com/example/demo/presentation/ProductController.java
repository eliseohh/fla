package com.example.demo.presentation;

import com.example.demo.domain.request.ProductRequest;
import com.example.demo.domain.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
@SuppressWarnings("rawtypes")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Mono<Object> create(@Valid @RequestBody Mono<ProductRequest> productRequest) {
        return productService.saveProduct(productRequest);
    }

    @GetMapping("/{sku}")
    public Mono<ResponseEntity> obtainProductBySku(@PathVariable String sku) {
        return productService.findProductBySku(sku);
    }

    @PutMapping("/")
    public Mono updateProduct(@Valid @RequestBody Mono<ProductRequest> productRequest) {
        return productService.updateProduct(productRequest);
    }

    @DeleteMapping("/{sku}")
    public Mono deleteProduct(@PathVariable String sku) {
        return productService.deleteProduct(sku);
    }
}
