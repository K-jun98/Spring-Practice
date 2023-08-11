package sample.cafekiosk.dto;

import java.util.List;

public record OrderCreateRequest(List<String> productNumbers) {
}
