package sample.cafekiosk.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.domain.order.Order;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.stock.Stock;
import sample.cafekiosk.dto.OrderCreateRequest;
import sample.cafekiosk.dto.OrderResponse;
import sample.cafekiosk.repository.OrderRepository;
import sample.cafekiosk.repository.ProductRepository;
import sample.cafekiosk.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.productNumbers();
        List<Product> products = findProductsBy(productNumbers);

        List<String> stockProductNumbers = products.stream()
                .filter(product -> product.getType().isStockType())
                .map(Product::getProductNumber)
                .toList();

        List<Stock> stocks = stockRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Stock> stockMap = stocks.stream()
                .collect(toMap(Stock::getProductNumber, Function.identity()));

        Map<String, Long> productCountiongMap = stockProductNumbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()));

        for (String stockProductNumber : productCountiongMap.keySet()) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountiongMap.get(stockProductNumber).intValue();
            stock.deductQuantity(quantity);
        }

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(toMap(Product::getProductNumber, Function.identity()));

        List<Product> duplicateProducts = productNumbers.stream()
                .map(productNumber -> productMap.get(productNumber))
                .toList();
        return duplicateProducts;
    }

}
