package dev.rumble.cafe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CoffeeOrderResDto {
    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    public CoffeeOrderResDto(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
