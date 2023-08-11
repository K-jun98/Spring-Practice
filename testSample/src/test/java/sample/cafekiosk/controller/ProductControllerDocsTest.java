package sample.cafekiosk.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.docs.RestDocsSupport;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;
import sample.cafekiosk.dto.ProductCreateRequest;
import sample.cafekiosk.dto.ProductResponse;
import sample.cafekiosk.service.ProductService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;

@WebMvcTest(controllers = ProductController.class)
public class ProductControllerDocsTest extends RestDocsSupport {

    @MockBean
    private ProductService productService;


    @DisplayName("신규상품을 등록하는 API")
    @Test
    void test() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        given(productService.createProduct(any(ProductCreateRequest.class)))
                .willReturn(ProductResponse.builder()
                        .id(1L)
                        .productNumber("001")
                        .type(ProductType.HANDMADE)
                        .sellingStatus(ProductSellingStatus.SELLING)
                        .name("아메리카노")
                        .price(4000)
                        .build()
                );

        mockMvc.perform(post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("product-create",
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("상품 타입"),
                                fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품 타입"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("상품 아이디"),
                                fieldWithPath("productNumber").type(JsonFieldType.STRING)
                                        .description("상품 번ㄸ"),
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("상품 타입"),
                                fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품 싱태"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("가격")
                        )
                ));
    }
}
