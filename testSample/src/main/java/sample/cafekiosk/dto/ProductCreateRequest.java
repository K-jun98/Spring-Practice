package sample.cafekiosk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;

@Builder
public record ProductCreateRequest(
        @NotNull(message = "상품 타입은 필수입니다.")
        ProductType type,
        @NotNull(message = "상품 판매 상태는 필수입니다.")
        ProductSellingStatus sellingStatus,
        @NotBlank(message = "상품 이름은 필수입니다.")
        String name,
        @Positive(message = "상품 가격은 양수여야 합니다.")
        int price
) {
}
