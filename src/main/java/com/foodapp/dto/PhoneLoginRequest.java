package com.foodapp.dto;

public class PhoneLoginRequest {
    private String phone;
    private String name; // optional - only used the first time this phone orders

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
