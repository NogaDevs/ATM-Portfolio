package com.atm.session;

import com.atm.domain.Role;

import java.time.LocalDateTime;

public class SessionManagerImpl implements  SessionManager{

    private SessionPrincipal activeSession;

    @Override
    public void createSession(int customerId, String name, String role) {
        activeSession = new SessionPrincipal(customerId, name, role);
        touch();
    }

    @Override
    public void logout() {
        activeSession = null;
    }

    @Override
    public void touch() {
        activeSession.setTimeout();
    }

    @Override
    public SessionPrincipal requireActive() {
        if (activeSession == null) {
            throw new NoActiveSessionException("No active session currently.");
        }
        if (activeSession.getTimeout().isBefore(LocalDateTime.now())) {
            logout();
            throw new SessionExpiredException("Session has expired, login again");
        }
        return activeSession;
    }

    public SessionPrincipal getActiveSession() {return activeSession;}

}
