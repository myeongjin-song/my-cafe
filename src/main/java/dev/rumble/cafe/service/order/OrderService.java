package dev.rumble.cafe.service.order;

import dev.rumble.cafe.model.dto.CoffeeOrderDto;
import dev.rumble.cafe.model.entity.Order;

public interface OrderService {
    Order placeOrder(CoffeeOrderDto orderRequest);
    void cancelOrder(Long orderId);
}
