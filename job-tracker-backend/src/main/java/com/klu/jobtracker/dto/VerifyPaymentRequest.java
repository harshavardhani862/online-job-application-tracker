package com.klu.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyPaymentRequest {
    @NotBlank private String razorpayOrderId;
    @NotBlank private String razorpayPaymentId;
    @NotBlank private String razorpaySignature;

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpayOrderId(String v) { razorpayOrderId = v; }
    public void setRazorpayPaymentId(String v) { razorpayPaymentId = v; }
    public void setRazorpaySignature(String v) { razorpaySignature = v; }
}
