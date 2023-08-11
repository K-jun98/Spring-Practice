package sample.cafekiosk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.cafekiosk.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
