package sample.cafekiosk.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static sample.cafekiosk.domain.product.ProductType.BOTTLE;


class ProductTypeTest {

    @DisplayName("재고차감을 해야하는 타입인지 체크한다.")
    @Test
    void containsStockTypeTest() {
        Assertions.assertThat(BOTTLE.isStockType()).isTrue();
    }

}
