package com.atm.session;

import com.atm.domain.Role;

public interface SessionManager {

    public void createSession(int customerId, String name, String role);
    public void logout();
    public void touch();
    public SessionPrincipal requireActive();

}
