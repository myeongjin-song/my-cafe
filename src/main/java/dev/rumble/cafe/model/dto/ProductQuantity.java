package dev.rumble.cafe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
public class ProductQuantity {
    @Schema(description = "제품 ID", example = "1")
    private Long productId;
    @Schema(description = "수량", example = "2")
    private int quantity;

    public ProductQuantity(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
