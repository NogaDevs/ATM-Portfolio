package com.atm.session;

import com.atm.domain.Role;
import java.time.LocalDateTime;

public class SessionPrincipal {

    long LOGOUT_MINUTES = 15;

    private final int customerId;
    private final String name;
    private final String role;
    private final LocalDateTime loginTime;
    private LocalDateTime lastUpdate;
    private LocalDateTime timeout;

    public SessionPrincipal(int id, String name, String role) {

        this.customerId = id;
        this.name = name;
        this.role = role;
        this.loginTime = LocalDateTime.now();
        this.lastUpdate = loginTime;
        this.timeout = LocalDateTime.now().plusMinutes(LOGOUT_MINUTES);

    }

    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return name; };
    public String getRole() { return role; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getTimeout() { return timeout; }

    public void setTimeout() {
        this.timeout = lastUpdate.plusMinutes(LOGOUT_MINUTES);
        this.lastUpdate = LocalDateTime.now();
    }

}
