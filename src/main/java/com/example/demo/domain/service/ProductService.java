package com.example.demo.domain.service;

import com.example.demo.data.entity.Product;
import com.example.demo.data.repository.ProductRepository;
import com.example.demo.domain.ProductMapper;
import com.example.demo.domain.dto.MessageDTO;
import com.example.demo.domain.request.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.AssertTrue;

@Service
@SuppressWarnings("rawtypes")
public class ProductService {
    private static final String REGEX_SKU = "[FAL]+[-]+\\d+$";
    private static final String REGEX_MESSAGE_ERROR = "sku: Invalid sku format";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    @AssertTrue
    public Mono<Object> saveProduct(Mono<ProductRequest> productRequest) {
        return productRequest.flatMap(el -> {
            if (el.getId() != null)
                return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "id must be null"));

            var s = Integer.parseInt(el.getSku().split("-")[1]);
            if (s < 1000000 || s > 99999999)
                return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "sku range is bad"));

            Mono<Product> bySku = productRepository.findBySku(el.getSku());
            return bySku
                    .flatMap(__ -> Mono.error(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "sku already taken")))
                    .switchIfEmpty(Mono.defer(() -> productRepository.save(productMapper.apply(el))));
        });
    }

    public Mono<ResponseEntity> findProductBySku(String sku) {
        if (!sku.matches(REGEX_SKU))
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, REGEX_MESSAGE_ERROR));

        var s = Integer.parseInt(sku.split("-")[1]);
        if (s < 1000000 || s > 99999999)
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "sku range is bad"));

        Mono<Product> bySku = productRepository.findBySku(sku);
        return bySku
                .map(el -> ResponseEntity.status(HttpStatus.OK).body(el))
                .cast(ResponseEntity.class)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @Transactional
    public Mono updateProduct(Mono<ProductRequest> productRequest) {
        return productRequest.flatMap(el -> productRepository.findById(el.getId()).flatMap(inner -> {
                    if (inner.getId() != null)
                        return productRepository.save(inner);
                    else
                        return Mono.error(new HttpClientErrorException(HttpStatus.GONE, "element does not exists"));
                }
        ));
    }

    @Transactional
    public Mono deleteProduct(String sku) {
        if (!sku.matches(REGEX_SKU))
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, REGEX_MESSAGE_ERROR));

        var s = Integer.parseInt(sku.split("-")[1]);
        if (s < 1000000 || s > 99999999)
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "sku range is bad"));

        return productRepository.deleteBySku(sku).flatMap(el -> {
            if (el)
                return Mono.just(ResponseEntity.ok().body(new MessageDTO("deleted")));
            else
                return Mono.error(new HttpClientErrorException(HttpStatus.NOT_FOUND, "already deleted"));
        });
    }
}
