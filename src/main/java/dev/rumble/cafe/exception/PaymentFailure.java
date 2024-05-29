package dev.rumble.cafe.exception;

public class PaymentFailure extends RuntimeException {
    public PaymentFailure(String message) {
        super(message);
    }
}