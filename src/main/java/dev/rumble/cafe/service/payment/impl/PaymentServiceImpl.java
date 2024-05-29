package dev.rumble.cafe.service.payment.impl;

import dev.rumble.cafe.service.payment.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public String makePayment() throws Exception {
        Thread.sleep((long)(Math.random() * 1000));

        Random random = new Random();
        if(random.nextInt(2) == 1) {
            throw new Exception("Failed!");
        }
        return "Success!";
    }
}
