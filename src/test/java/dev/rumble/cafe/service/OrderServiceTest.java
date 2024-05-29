package dev.rumble.cafe.service;

import dev.rumble.cafe.exception.ProductOutOfStockException;
import dev.rumble.cafe.model.dto.CoffeeOrderDto;
import dev.rumble.cafe.model.dto.ProductQuantity;
import dev.rumble.cafe.model.entity.Payment;
import dev.rumble.cafe.model.entity.Product;
import dev.rumble.cafe.model.entity.User;
import dev.rumble.cafe.repository.*;
import dev.rumble.cafe.service.order.impl.OrderServiceImpl;
import dev.rumble.cafe.service.payment.PaymentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private User user;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("John Doe");
        user.setPhone("1234567890");
        user.setGender("Male");
        user.setBirthDate(LocalDateTime.now().toLocalDate());
        userRepository.save(user);

        product1 = new Product();
        product1.setPrice(10);
        product1.setName("Americano");
        product1.setStockQuantity(5);
        productRepository.save(product1);

        product2 = new Product();
        product2.setPrice(20);
        product2.setName("Latte");
        product2.setStockQuantity(3);
        productRepository.save(product2);
    }

    @Test
    @Transactional
    @DisplayName("주문 성공")
    public void testPlaceOrder() throws Exception {
        when(paymentService.makePayment()).thenReturn("Success");

        CoffeeOrderDto orderDTO = new CoffeeOrderDto();
        orderDTO.setUserId(user.getId());
        orderDTO.setItems(Arrays.asList(
                new ProductQuantity(product1.getId(), 2),
                new ProductQuantity(product2.getId(), 1)
        ));

        Long orderId = orderService.placeOrder(orderDTO).getId();

        assertNotNull(orderId);
        assertEquals(3, productRepository.findById(product1.getId()).get().getStockQuantity());
        assertEquals(2, productRepository.findById(product2.getId()).get().getStockQuantity());
    }

    @Test
    @Transactional
    @DisplayName("재고 부족으로 인한 주문 실패")
    public void testPlaceOrderProductOutOfStock() {
        product1.setStockQuantity(0);
        productRepository.save(product1);

        CoffeeOrderDto orderDTO = new CoffeeOrderDto();
        orderDTO.setUserId(user.getId());
        orderDTO.setItems(Arrays.asList(
                new ProductQuantity(product1.getId(), 1)
        ));

        Exception exception = assertThrows(ProductOutOfStockException.class, () -> {
            orderService.placeOrder(orderDTO);
        });

        assertEquals("Insufficient stock for product: Americano", exception.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("주문 취소")
    public void testCancelOrder() throws Exception {
        when(paymentService.makePayment()).thenReturn("Success");

        CoffeeOrderDto orderDTO = new CoffeeOrderDto();
        orderDTO.setUserId(user.getId());
        orderDTO.setItems(Arrays.asList(
                new ProductQuantity(product1.getId(), 2),
                new ProductQuantity(product2.getId(), 1)
        ));
        // 입력받은 데이터를 List를 set 객체로 변경 필요

        Long orderId = orderService.placeOrder(orderDTO).getId();

        orderService.cancelOrder(orderId);

        assertEquals(5, productRepository.findById(product1.getId()).get().getStockQuantity()); // 재고가 다시 5로 복구됨
        assertEquals(3, productRepository.findById(product2.getId()).get().getStockQuantity()); // 재고가 다시 3로 복구됨
        Payment payment = paymentRepository.findByOrderId(orderId);
        assertTrue(payment.isRefunded());
    }

    @Test
    @DisplayName("결제 실패로 인한 주문 실패")
    public void testPlaceOrderPaymentFailure() throws Exception {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            when(paymentService.makePayment()).thenThrow(new Exception("Failed!"));
            CoffeeOrderDto orderDTO = new CoffeeOrderDto();
            orderDTO.setUserId(user.getId());
            orderDTO.setItems(Arrays.asList(
                    new ProductQuantity(product1.getId(), 2)
            ));
            orderService.placeOrder(orderDTO);
        } catch (Exception e) {
            // 롤백 실행
            transactionManager.rollback(status);
        }

        // 새 트랜잭션 시작
        TransactionStatus newStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Product product = productRepository.findById(product1.getId()).orElseThrow();
            assertEquals(5, product.getStockQuantity(), "재고가 롤백 후 원래대로 복구");
        } finally {
            // 새 트랜잭션 롤백
            transactionManager.rollback(newStatus);
        }
    }
}