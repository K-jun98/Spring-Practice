package sample.cafekiosk.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockTest {

    @DisplayName("재고의 수량이 요구된 수량보다 많다면 수량을 차감한다.")
    @Test
    void deductQuantityTest() {
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

        stock.deductQuantity(quantity);

        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}
