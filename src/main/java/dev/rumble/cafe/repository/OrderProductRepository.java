package dev.rumble.cafe.repository;

import dev.rumble.cafe.model.entity.Order;
import dev.rumble.cafe.model.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query("SELECT op FROM OrderProduct op JOIN FETCH op.product WHERE op.order = :order")
    List<OrderProduct> findByOrderWithProducts(@Param("order") Order order);
}
