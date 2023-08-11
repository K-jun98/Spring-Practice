package sample.cafekiosk.dto;

import sample.cafekiosk.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id,
                            int totalPrice,
                            LocalDateTime registeredDateTime,
                            List<ProductResponse> products
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getRegisteredDateTime(),
                mapToProductResponse(order));
    }

    private static List<ProductResponse> mapToProductResponse(Order order) {
        return order.getOrderProducts()
                .stream()
                .map(orderProduct -> ProductResponse.from(orderProduct.getProduct()))
                .toList();
    }

}
