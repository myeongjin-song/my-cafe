package dev.rumble.cafe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProductInfoResDto extends ProductQuantity {
    @Schema(description = "상품명", example = "Americano")
    private String productName;
    public ProductInfoResDto(Long productId, int quantity, String productName) {
        super(productId, quantity);
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
