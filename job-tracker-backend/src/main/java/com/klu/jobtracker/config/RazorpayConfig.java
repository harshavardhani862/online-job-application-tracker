package com.klu.jobtracker.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class RazorpayConfig {
    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Bean
    @Lazy
    public RazorpayClient razorpayClient() throws Exception {
        if (keyId == null || keyId.isBlank()
                || keySecret == null || keySecret.isBlank()) {
            throw new IllegalStateException(
                "Razorpay is not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }
        return new RazorpayClient(keyId.trim(), keySecret.trim());
    }

    public String getKeyId() { return keyId; }
    public String getKeySecret() { return keySecret; }
}
