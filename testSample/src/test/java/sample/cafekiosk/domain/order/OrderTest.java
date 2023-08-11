package sample.cafekiosk.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static sample.cafekiosk.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

class OrderTest {

    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
    @Test
    void calculateTotalPrice() {
        List<Product> products = List.of(createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000),
                createdProduct("002", HOLD, BAKERY, "과자", 2000),
                createdProduct("003", SELLING, BAKERY, "아메리카노", 4000));

        Order order = Order.create(products, LocalDateTime.now());

        assertThat(order.getTotalPrice()).isEqualTo(7000);
    }

    @DisplayName("주문 생성 시 초기 주문 상태는 INIT이다.")
    @Test
    void initTest() {
        List<Product> products = List.of(createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000),
                createdProduct("002", HOLD, BAKERY, "과자", 2000),
                createdProduct("003", SELLING, BAKERY, "아메리카노", 4000));

        Order order = Order.create(products, LocalDateTime.now());

        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.INIT);
    }

    @DisplayName("주문 생성 시 등록 시간을 넣어준다.")
    @Test
    void registeredDateTimeTest() {
        List<Product> products = List.of(createdProduct("001", STOP_SELLING, HANDMADE, "음료수", 1000),
                createdProduct("002", HOLD, BAKERY, "과자", 2000),
                createdProduct("003", SELLING, BAKERY, "아메리카노", 4000));

        LocalDateTime dateTime = LocalDateTime.now();
        Order order = Order.create(products, dateTime);

        assertThat(order.getRegisteredDateTime()).isEqualTo(dateTime);
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
