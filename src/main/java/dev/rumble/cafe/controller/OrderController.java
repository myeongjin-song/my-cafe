package dev.rumble.cafe.controller;

import dev.rumble.cafe.model.dto.ApiResult;
import dev.rumble.cafe.model.dto.CoffeeOrderDto;
import dev.rumble.cafe.model.dto.CoffeeOrderResDto;
import dev.rumble.cafe.model.entity.Order;
import dev.rumble.cafe.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static dev.rumble.cafe.model.dto.ApiResult.OK;

@RestController
@RequestMapping("/orders")
@Validated
@Tag(name = "Order", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResult.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"response\":{\"orderId\":1},\"error\":null}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"success\":false,\"response\":null,\"error\":\"Validation Error\"}")))
    })
    @PostMapping
    public ApiResult<CoffeeOrderResDto> createOrder(@Valid @RequestBody CoffeeOrderDto orderRequest) {
        Order order =  orderService.placeOrder(orderRequest);
        return OK(new CoffeeOrderResDto(order.getId()));
    }

    @Operation(summary = "주문 취소", description = "기존 주문을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"success\":true,\"response\":null,\"error\":null}"))),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"success\":false,\"response\":null,\"error\":\"Order not found\"}")))
    })
    @DeleteMapping("/{id}")
    public void cancelOrder(@Parameter(description = "주문 ID", required = true)@PathVariable Long id){
        orderService.cancelOrder(id);
    }
}
