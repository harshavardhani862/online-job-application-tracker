package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.*;
import com.klu.jobtracker.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(
            Principal p, @RequestBody(required = false) CreateOrderRequest request) throws Exception {
        if (request == null) request = new CreateOrderRequest();
        return service.createOrder(p.getName(), request);
    }

    @PostMapping("/verify")
    public PaymentResponse verify(
            Principal p, @Valid @RequestBody VerifyPaymentRequest request) throws Exception {
        return service.verify(p.getName(), request);
    }

    @GetMapping("/my-payments")
    public List<PaymentResponse> history(Principal p) {
        return service.history(p.getName());
    }
}
