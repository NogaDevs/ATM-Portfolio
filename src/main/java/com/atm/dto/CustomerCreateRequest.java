package com.atm.dto;
import java.math.BigDecimal;

public class CustomerCreateRequest {

    private final String name;
    private final String cardNumber;
    private final char[] plainPin;
    private final String email;
    private final BigDecimal startingBalance;

    public CustomerCreateRequest(String name, String cardNumber, char[] plainPin, String email, BigDecimal startingBalance) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.plainPin = plainPin;
        this.email = email;
        this.startingBalance = startingBalance;
    }

    public String getName(){return this.name;}
    public String getCardNumber(){return this.cardNumber;}
    public char[] getPlainPin(){return this.plainPin;}
    public String getEmail(){return this.email;}
    public BigDecimal getStartingBalance(){return this.startingBalance;}

}
