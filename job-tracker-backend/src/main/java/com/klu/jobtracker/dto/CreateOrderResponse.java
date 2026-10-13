package com.klu.jobtracker.dto;

public class CreateOrderResponse {
    private String orderId;
    private Long amount;
    private String currency;
    private String keyId;

    public CreateOrderResponse(String orderId, Long amount, String currency, String keyId) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.keyId = keyId;
    }

    public String getOrderId() { return orderId; }
    public Long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getKeyId() { return keyId; }
}
