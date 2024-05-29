package dev.rumble.cafe.repository;

import dev.rumble.cafe.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.products WHERE o.id = :orderId")
    Order findOrderWithProducts(Long orderId);
}
