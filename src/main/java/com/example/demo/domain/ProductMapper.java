package com.example.demo.domain;

import com.example.demo.data.entity.Product;
import com.example.demo.domain.request.ProductRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements Function<ProductRequest, Product> {

    @Override
    @Validated
    public Product apply(ProductRequest productRequest) {
        var product = new Product(productRequest.getSku(),
                productRequest.getName(),
                productRequest.getBrand(),
                productRequest.getSize(),
                productRequest.getPrice(),
                productRequest.getPrincipalImage(),
                obtainUrl(productRequest.getOtherImages()));

        if (productRequest.getId() != null && productRequest.getId() > 0L)
            product.setId(productRequest.getId());
        return product;
    }

    private String obtainUrl(List<String> otherImages) {
        return otherImages == null || otherImages.isEmpty() ? null : String.join(";", otherImages);
    }
}
