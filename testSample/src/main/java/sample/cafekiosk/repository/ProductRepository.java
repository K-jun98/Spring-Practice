package sample.cafekiosk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySellingStatusIn(List<ProductSellingStatus> sellingStatuses);

    List<Product> findAllByProductNumberIn(List<String> productNumbers);

    @Query(value = "select p.product_number from products p order by id desc limit 1", nativeQuery = true)
    Optional<String> findLatestProductNumber();
}
