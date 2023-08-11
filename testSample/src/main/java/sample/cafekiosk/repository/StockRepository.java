package sample.cafekiosk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.cafekiosk.domain.stock.Stock;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findAllByProductNumberIn(List<String> productNumbers);
}
