package com.klu.jobtracker.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String subscriptionStatus;
    private UserResponse user;

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.subscriptionStatus = user.getSubscriptionStatus();
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubscriptionStatus() { return subscriptionStatus; }
    public UserResponse getUser() { return user; }
}
