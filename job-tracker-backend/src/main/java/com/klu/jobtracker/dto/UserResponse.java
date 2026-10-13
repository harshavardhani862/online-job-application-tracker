package com.klu.jobtracker.dto;

import com.klu.jobtracker.entity.User;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String subscriptionStatus;

    public UserResponse(User u) {
        this.id = u.getId();
        this.name = u.getName();
        this.email = u.getEmail();
        this.phone = u.getPhone();
        this.subscriptionStatus = u.getSubscriptionStatus().name();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSubscriptionStatus() { return subscriptionStatus; }
}
