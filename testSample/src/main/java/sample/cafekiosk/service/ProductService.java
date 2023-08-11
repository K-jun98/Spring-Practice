package sample.cafekiosk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.dto.ProductCreateRequest;
import sample.cafekiosk.dto.ProductResponse;
import sample.cafekiosk.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getSellingProduct() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
        String nextProductNumber = getNextProductNumber();
        Product product = Product.builder()
                .productNumber(nextProductNumber)
                .type(request.type())
                .sellingStatus(request.sellingStatus())
                .name(request.name())
                .price(request.price())
                .build();
        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    private String getNextProductNumber() {
        Optional<String> latestProductNumber = productRepository.findLatestProductNumber();
        String productNumber = latestProductNumber.orElseGet(() -> "000");
        int latestProductNumberInt = Integer.valueOf(productNumber);
        int nextProductNumber = latestProductNumberInt + 1;
        return String.format("%03d", nextProductNumber);
    }
}
