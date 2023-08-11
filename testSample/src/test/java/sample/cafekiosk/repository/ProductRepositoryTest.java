package sample.cafekiosk.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static sample.cafekiosk.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("선택한 판매상태의 조건들을 찾아 반환한다.")
    @Test
    void findAllBySellingStatusInTest() {
        Product 음료수 = createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000);
        Product 빵 = createdProduct("002", HOLD, BAKERY, "과자", 2000);
        Product 아메리카노 = createdProduct("003", SELLING, BAKERY, "아메리카노", 4000);

        productRepository.saveAll(List.of(음료수, 빵, 아메리카노));

        List<Product> foundProduct = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        assertThat(foundProduct).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("002", "과자", HOLD),
                        tuple("003", "아메리카노", SELLING)
                );
    }

    @DisplayName("프로덕트의 제품번호로 프로덕트를 조회한다.")
    @Test
    void findAllByProductNumberInTest() {
        Product 음료수 = createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000);
        Product 빵 = createdProduct("002", HOLD, BAKERY, "과자", 2000);
        Product 아메리카노 = createdProduct("003", SELLING, BAKERY, "아메리카노", 4000);

        productRepository.saveAll(List.of(음료수, 빵, 아메리카노));

        List<Product> foundProduct = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        assertThat(foundProduct).hasSize(2)
                .extracting("productNumber")
                .containsExactlyInAnyOrder("001", "002");
    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 조회한다.")
    @Test
    void findLatestProductNumberTest() {
        Product 음료수 = createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000);
        Product 빵 = createdProduct("002", HOLD, BAKERY, "과자", 2000);
        Product 아메리카노 = createdProduct("003", SELLING, BAKERY, "아메리카노", 4000);

        productRepository.saveAll(List.of(음료수, 빵, 아메리카노));

        String latestProductNumber = productRepository.findLatestProductNumber().get();

        assertThat(latestProductNumber).isEqualTo("003");
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
