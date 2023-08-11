package sample.cafekiosk.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;
import sample.cafekiosk.dto.ProductCreateRequest;
import sample.cafekiosk.dto.ProductResponse;
import sample.cafekiosk.repository.ProductRepository;

import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1을 증가한 겂이다.")
    @Test
    void createProductTest() {
        Product 음료수 = createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000);
        productRepository.save(음료수);

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        ProductResponse product = productService.createProduct(request);

        Assertions.assertThat(product)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("002", HANDMADE, SELLING, "카푸치노", 5000);
    }

    @DisplayName("상품이 없을때 신규 상품을 등록하면 상품번호는 001이다.")
    @Test
    void createProductWhenProductEmptyTest() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        ProductResponse product = productService.createProduct(request);

        Assertions.assertThat(product)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("001", HANDMADE, SELLING, "카푸치노", 5000);
    }

    private Product createdProduct(String number, ProductSellingStatus status, ProductType type, String name, int price) {
        Product product = Product.builder()
                .productNumber(number)
                .sellingStatus(status)
                .type(type)
                .name(name)
                .price(price)
                .build();
        return product;
    }
}
