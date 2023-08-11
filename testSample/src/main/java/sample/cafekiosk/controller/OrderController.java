package sample.cafekiosk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.dto.OrderCreateRequest;
import sample.cafekiosk.dto.OrderResponse;
import sample.cafekiosk.service.OrderService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("api/v1/orders/new")
    public OrderResponse createOrder(@RequestBody OrderCreateRequest request) {
        request.productNumbers().forEach(System.out::println);
        LocalDateTime requestTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, requestTime);

        return orderResponse;
    }
}
