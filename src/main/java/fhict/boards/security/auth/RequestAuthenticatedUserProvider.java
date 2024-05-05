package fhict.boards.security.auth;

import fhict.boards.security.token.AccessToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

/**
 * Provides access to the authenticated user's access token within a web request.
 */
@Configuration
public class RequestAuthenticatedUserProvider {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken getAuthenticatedUserInRequest() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null; // No security context available.
        }

        final Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null; // No user authenticated for the current request.
        }

        final Object details = authentication.getDetails();
        if (!(details instanceof AccessToken)) {
            return null; // User's details do not contain an AccessToken.
        }

        return (AccessToken) authentication.getDetails(); // Return the user's AccessToken
    }
}
