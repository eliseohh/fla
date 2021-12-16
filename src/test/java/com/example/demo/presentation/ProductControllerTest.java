package com.example.demo.presentation;

import com.example.demo.data.entity.Product;
import com.example.demo.data.repository.ProductRepository;
import com.example.demo.domain.ProductMapper;
import com.example.demo.domain.request.ProductRequest;
import com.example.demo.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ProductController.class)
@Import(ProductService.class)
class ProductControllerTest {

    @MockBean
    ProductRepository productRepository;

    @SpyBean
    ProductMapper productMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void givenSomeProductRequestWithSkuAlreadyTaken() {

        //arrange
        ProductRequest productRequest = ProductRequest.builder()
                .brand("some_brand")
                .name("some_name")
                .price(1.0)
                .sku("FAL-1000000")
                .size("L")
                .build();

        Product mockedProduct = new Product("some_sku", "some_name", "some_brand", "some_size", 1.0, null, null);

        Mockito.when(productRepository.findBySku(anyString())).thenReturn(Mono.just(mockedProduct));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(Mono.just(new Product()));

        //act and assert
        webTestClient
                .post()
                .uri("/product/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productRequest))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void givenSomeProductRequestItWillSave() {
        //arrange
        ProductRequest productRequest = ProductRequest.builder()
                .brand("some_brand")
                .name("some_name")
                .price(1.0)
                .sku("FAL-1000000")
                .size("L")
                .build();

        Mockito.when(productRepository.findBySku(anyString())).thenReturn(Mono.empty());
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(Mono.just(new Product()));

        //act and assert
        webTestClient
                .post()
                .uri("/product/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productRequest))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void givenSomeInvalidSkuWillReturnHttp400() {
        //arrange
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .brand("some_brand")
                .name("some_name")
                .price(1.0)
                .sku("FAL-1000000")
                .size("L")
                .build();

        //act and assert
        webTestClient
                .post()
                .uri("/product/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenSomeSkuItWillReturnAHttp400() {
        //arrange
        String sku = "some_sku";

        //act and assert
        webTestClient
                .get()
                .uri("/product/" + sku)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenSomeSkuItWillReturnAProduct() {
        //arrange
        String sku = "FAL-1000000";

        Mockito.when(productRepository.findBySku(anyString())).thenReturn(Mono.just(new Product()));

        //act and assert
        webTestClient
                .get()
                .uri("/product/" + sku)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void givenSomeSkuItWillReturnNoContent() {
        //arrange
        String sku = "FAL-1000000";

        Mockito.when(productRepository.findBySku(anyString())).thenReturn(Mono.empty());

        //act and assert
        webTestClient
                .get()
                .uri("/product/" + sku)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenSomeProductItWillUpdate() {
        //arrange
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .brand("some_brand")
                .name("some_name")
                .price(1.0)
                .sku("FAL-1000000")
                .size("L")
                .build();

        Product mockedProduct = new Product("some_sku", "some_name", "some_brand", "some_size", 1.0, null, null);
        mockedProduct.setId(1L);
        Mockito.when(productRepository.findById(anyLong())).thenReturn(Mono.just(mockedProduct));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(Mono.just(mockedProduct));

        //act and assert
        webTestClient
                .put()
                .uri("/product/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productRequest))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void givenSomeProductItWillReturnAlreadyGone() {
        //arrange
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .brand("some_brand")
                .name("some_name")
                .price(1.0)
                .sku("FAL-1000000")
                .size("L")
                .build();

        Product mockedProduct = new Product("some_sku", "some_name", "some_brand", "some_size", 1.0, null, null);
        Mockito.when(productRepository.findById(anyLong())).thenReturn(Mono.just(mockedProduct));

        //act and assert
        webTestClient
                .put()
                .uri("/product/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productRequest))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GONE);
    }

    @Test
    void deleteProductSuccess() {
        //arrange
        String sku = "FAL-1000000";

        Mockito.when(productRepository.deleteBySku(anyString())).thenReturn(Mono.just(Boolean.TRUE));
        //act and assert
        webTestClient
                .delete()
                .uri("/product/"+sku)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);
    }
}