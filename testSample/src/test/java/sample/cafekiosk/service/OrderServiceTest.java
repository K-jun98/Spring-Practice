package sample.cafekiosk.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;
import sample.cafekiosk.domain.stock.Stock;
import sample.cafekiosk.dto.OrderCreateRequest;
import sample.cafekiosk.dto.OrderResponse;
import sample.cafekiosk.repository.OrderProductRepository;
import sample.cafekiosk.repository.OrderRepository;
import sample.cafekiosk.repository.ProductRepository;
import sample.cafekiosk.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static sample.cafekiosk.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.domain.product.ProductType.BOTTLE;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setProduct() {
        Product 음료수 = createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000);
        Product 빵 = createdProduct("002", HOLD, BAKERY, "과자", 2000);
        Product 아메리카노 = createdProduct("003", SELLING, BOTTLE, "아메리카노", 3000);

        productRepository.saveAll(List.of(음료수, 빵, 아메리카노));
    }

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        OrderCreateRequest request = new OrderCreateRequest(List.of("001", "001"));

        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        assertThat(orderResponse.id()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 2000);

        assertThat(orderResponse.products()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        Stock stock1 = Stock.create("002", 2);
        Stock stock2 = Stock.create("003", 3);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = new OrderCreateRequest(List.of("003", "002", "002", "003"));
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("002", 0),
                        tuple("003", 1)
                );
    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateNumbers() {
        OrderCreateRequest request = new OrderCreateRequest(List.of("001", "001"));

        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        assertThat(orderResponse.id()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 2000);

        assertThat(orderResponse.products()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
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
