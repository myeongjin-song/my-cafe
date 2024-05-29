package dev.rumble.cafe.controller;

import dev.rumble.cafe.model.dto.ProductInfoResDto;
import dev.rumble.cafe.model.dto.ProductQuantity;
import dev.rumble.cafe.model.entity.Product;
import dev.rumble.cafe.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "제품 관련 API")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "모든 제품 조회", description = "모든 제품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 제품 목록을 조회했습니다..",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductInfoResDto.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"stockQuantity\":100,\"name\":\"Espresso\"},{\"id\":2,\"stockQuantity\":50,\"name\":\"Latte\"}]"))),
    })
    @GetMapping
    public List<ProductInfoResDto> getAllProducts() {
        return productService.getAllProducts().stream().map(product -> new ProductInfoResDto(
                product.getId(),
                product.getStockQuantity(),
                product.getName())).toList();
    }
}
