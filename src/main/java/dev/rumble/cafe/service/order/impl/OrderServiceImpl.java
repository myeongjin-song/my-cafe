package dev.rumble.cafe.service.order.impl;

import dev.rumble.cafe.exception.*;
import dev.rumble.cafe.model.dto.CoffeeOrderDto;
import dev.rumble.cafe.model.dto.ProductQuantity;
import dev.rumble.cafe.model.entity.*;
import dev.rumble.cafe.repository.*;
import dev.rumble.cafe.service.order.OrderService;
import dev.rumble.cafe.service.payment.PaymentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderProductRepository orderProductRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            PaymentRepository paymentRepository,
                            PaymentService paymentService){
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }


    @Override
    @Transactional
    public Order placeOrder(CoffeeOrderDto orderRequest){
        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderTime(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        int totalAmount = 0;
        for (ProductQuantity item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new ProductOutOfStockException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product); // product 수량 제거

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(savedOrder);

            Product currentItem = new Product();
            currentItem.setId(product.getId());
            currentItem.setName(product.getName());
            currentItem.setPrice(product.getPrice());
            currentItem.setStockQuantity(item.getQuantity()); // 주문 수량을 넣어준다.
            orderProduct.setProduct(currentItem);
            orderProductRepository.save(orderProduct);

            totalAmount += product.getPrice() * item.getQuantity();
        }

        // Mocking payment process
        String paymentResult = null;
        try {
            paymentResult = paymentService.makePayment();
            logger.info("payment: {}", paymentResult);
        } catch (Exception e) {
            throw new PaymentFailure("Payment failed");
        }

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalAmount);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        return savedOrder;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        if (order.isCancelled()) {
            throw new OrderAlreadyCancelledException("Order is already cancelled");
        }
        List<OrderProduct> orderMenuList = orderProductRepository.findByOrderWithProducts(order);

        order.setCancelled(true);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        for (OrderProduct orderProduct : orderMenuList) {
            Product remainProduct = productRepository.findById(orderProduct.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            remainProduct.setStockQuantity(remainProduct.getStockQuantity() + orderProduct.getProduct().getStockQuantity());
            productRepository.save(remainProduct);
        }

        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null && !payment.isRefunded()) {
            payment.setRefunded(true);
            payment.setRefundTime(LocalDateTime.now());
            paymentRepository.save(payment); // dirty check 방지를 위해 equals 수정
        }
    }
}
