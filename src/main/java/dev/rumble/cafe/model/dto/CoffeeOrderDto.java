package dev.rumble.cafe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CoffeeOrderDto {
    @NotNull(message = "User ID cannot be null")
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @NotNull(message = "items cannot be null")
    @Size(min = 1, message = "At least one item should be provided")
    @Schema(description = "주문 상품 목록")
    private List<ProductQuantity> items;

    public CoffeeOrderDto() {
    }

    public CoffeeOrderDto(Long userId, List<ProductQuantity> items) {
        this.userId = userId;
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ProductQuantity> getItems() {
        return items;
    }

    public void setItems(List<ProductQuantity> items) {
        this.items = items;
    }

}
