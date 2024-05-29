package dev.rumble.cafe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rumble.cafe.model.dto.CoffeeOrderDto;
import dev.rumble.cafe.model.dto.ProductQuantity;
import dev.rumble.cafe.model.entity.Order;
import dev.rumble.cafe.service.order.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;


    @Test
    @DisplayName("주문 API 테스트")
    public void testPlaceOrder() throws Exception {
        Long expectedOrderId = 1L;
        Order order = new Order();
        order.setId(expectedOrderId);
        when(orderService.placeOrder(any())).thenReturn(order);

        CoffeeOrderDto orderRequest = new CoffeeOrderDto();
        orderRequest.setUserId(1L);
        orderRequest.setItems(List.of(new ProductQuantity(1L,2)));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"success\":true,\"response\":{\"orderId\":1},\"error\":null}"));
    }

    @Test
    @DisplayName("주문취소 케이스")
    public void testCancelOrder() throws Exception {
        doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("userId 미존재로 인한 에러 리턴")
    public void testCreateOrderMissingUserId() throws Exception{
        CoffeeOrderDto orderRequest = new CoffeeOrderDto();
        orderRequest.setItems(List.of(new ProductQuantity(1L,2)));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수량 미존재로 인한 에러")
    public void testCreateOrderMissingItems() throws Exception{
        CoffeeOrderDto orderRequest = new CoffeeOrderDto();
        orderRequest.setUserId(1L);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }
}
