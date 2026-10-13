package com.klu.jobtracker.service;

import com.klu.jobtracker.config.RazorpayConfig;
import com.klu.jobtracker.dto.CreateOrderRequest;
import com.klu.jobtracker.dto.CreateOrderResponse;
import com.klu.jobtracker.dto.PaymentResponse;
import com.klu.jobtracker.dto.VerifyPaymentRequest;
import com.klu.jobtracker.entity.Payment;
import com.klu.jobtracker.entity.PaymentStatus;
import com.klu.jobtracker.entity.SubscriptionStatus;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.PaymentRepository;
import com.klu.jobtracker.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.premium-amount:49900}")
    private Long premiumAmount;

    public PaymentService(
            RazorpayClient razorpayClient,
            RazorpayConfig razorpayConfig,
            PaymentRepository paymentRepository,
            UserService userService,
            UserRepository userRepository) {

        this.razorpayClient = razorpayClient;
        this.razorpayConfig = razorpayConfig;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public CreateOrderResponse createOrder(
            String email,
            CreateOrderRequest request) throws Exception {

        User user = userService.getByEmail(email);

        // Price is controlled by the server; never trust a client-supplied amount.
        long amount = premiumAmount;
        if (request.getAmount() != null && request.getAmount() > 0
                && !request.getAmount().equals(premiumAmount)) {
            throw new IllegalArgumentException(
                    "Premium amount changed. Refresh the page and try again.");
        }

        JSONObject options = new JSONObject();

        options.put(
                "amount",
                amount
        );

        options.put(
                "currency",
                currency
        );

        options.put(
                "receipt",
                "premium_" + user.getId() + "_" + System.currentTimeMillis()
        );

        Order order = razorpayClient.orders.create(options);

        // This is YOUR database Payment entity
        Payment payment = new Payment();

        payment.setUser(user);
        payment.setUserEmail(user.getEmail());
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(PaymentStatus.CREATED);

        paymentRepository.save(payment);

        return new CreateOrderResponse(
                order.get("id"),
                amount,
                currency,
                razorpayConfig.getKeyId()
        );
    }

    public PaymentResponse verify(
            String email,
            VerifyPaymentRequest request) throws Exception {

        User user = userService.getByEmail(email);

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment order not found"
                        )
                );

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Payment does not belong to this user"
            );
        }

        JSONObject attributes = new JSONObject();

        attributes.put(
                "razorpay_order_id",
                request.getRazorpayOrderId()
        );

        attributes.put(
                "razorpay_payment_id",
                request.getRazorpayPaymentId()
        );

        attributes.put(
                "razorpay_signature",
                request.getRazorpaySignature()
        );

        boolean valid = Utils.verifyPaymentSignature(
                attributes,
                getSecret()
        );

        if (!valid) {

            payment.setStatus(PaymentStatus.FAILED);

            paymentRepository.save(payment);

            throw new IllegalArgumentException(
                    "Invalid Razorpay signature"
            );
        }

        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId()
        );

        payment.setRazorpaySignature(
                request.getRazorpaySignature()
        );

        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        // Upgrade user to premium
        user.setSubscriptionStatus(
                SubscriptionStatus.PREMIUM
        );

        userRepository.save(user);

        return new PaymentResponse(payment);
    }

    public List<PaymentResponse> history(String email) {

        User user = userService.getByEmail(email);

        return paymentRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(PaymentResponse::new)
                .toList();
    }

    private String getSecret() {
        String secret = razorpayConfig.getKeySecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "Razorpay is not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET.");
        }
        return secret.trim();
    }
}
