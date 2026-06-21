package com.atm.service;

import java.math.BigDecimal;

public interface AccountService {

    public BigDecimal deposit(int customerId, BigDecimal amount);
    public BigDecimal withdraw(int customerId, BigDecimal amount);
    public BigDecimal balance(int customerId);

}
