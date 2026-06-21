package com.atm.dto;


public class CustomerUpdateRequest {
    private final int customerId;
    private final String newName;
    private final String newCardNumber;
    private final char[] newPlanPin;
    private final String newEmail;

    public CustomerUpdateRequest(int customerId, String newName, String newCardNumber, char[] newPlanPin, String newEmail) {
        this.customerId = customerId;
        this.newName = newName;
        this.newCardNumber = newCardNumber;
        this.newPlanPin = newPlanPin;
        this.newEmail = newEmail;
    }

    public int getCustomerId(){return this.customerId;}
    public String getNewName(){return this.newName;}
    public String getNewCardNumber(){return this.newCardNumber;}
    public char[] getNewPlainPin(){return this.newPlanPin;}
    public String getNewEmail(){return this.newEmail;}
}
