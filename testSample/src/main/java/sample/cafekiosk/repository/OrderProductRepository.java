package sample.cafekiosk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.cafekiosk.domain.orderProduct.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
