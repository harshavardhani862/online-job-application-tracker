package com.klu.jobtracker.dto;

import com.klu.jobtracker.entity.Payment;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private String orderId;
    private String paymentId;
    private Long amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    public PaymentResponse(Payment p) {
        id = p.getId();
        orderId = p.getRazorpayOrderId();
        paymentId = p.getRazorpayPaymentId();
        amount = p.getAmount();
        currency = p.getCurrency();
        status = p.getStatus().name();
        createdAt = p.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getPaymentId() { return paymentId; }
    public Long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
