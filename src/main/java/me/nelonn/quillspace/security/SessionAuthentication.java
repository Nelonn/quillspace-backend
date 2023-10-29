package me.nelonn.quillspace.security;

import me.nelonn.quillspace.model.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class SessionAuthentication implements Authentication {
    private final Session session;
    private boolean authenticated = true;

    public SessionAuthentication(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.NO_AUTHORITIES;
    }

    @Override
    public Object getCredentials() {
        return session;
    }

    @Override
    public Object getDetails() {
        return session;
    }

    @Override
    public Object getPrincipal() {
        return session;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return session.getId();
    }
}
