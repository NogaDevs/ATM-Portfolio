package com.atm.dto;

public class AuthRecord {

    int customerId;
    String customerName;
    String customerRole;
    String hashedPin;
    int failedAttempts;
    boolean isLocked;

    public AuthRecord(int customerId, String customerName, String role, String hashedPin, int failedAttempts, boolean isLocked) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerRole = role;
        this.hashedPin = hashedPin;
        this.failedAttempts = failedAttempts;
        this.isLocked = isLocked;
    }

    public int getCustomerId() {
        return customerId;
    }
    public String getCustomerName() { return customerName; }
    public String getCustomerRole() { return customerRole; }
    public String getHashedPin() {
        return hashedPin;
    }
    public int getFailedAttempts() {
        return failedAttempts;
    }
    public boolean getIsLocked() {
        return isLocked;
    }

}
