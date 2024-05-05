package fhict.boards.security.auth;

import fhict.boards.exception.InvalidAccessTokenException;
import fhict.boards.security.token.AccessToken;
import fhict.boards.security.token.AccessTokenSerializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Custom Spring Security filter for handling user authentication via access tokens in the "Authorization" header.
 */
@RequiredArgsConstructor
@Component
public class AuthenticationRequestFilter extends OncePerRequestFilter {
    private final AccessTokenSerializer accessTokenSerializer;

    /**
     * Processes user authentication and access token validation for incoming HTTP requests.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String accessTokenString = requestTokenHeader.substring(7);

        try {
            AccessToken accessToken = accessTokenSerializer.decode(accessTokenString);
            setupSpringSecurityContext(accessToken);
            chain.doFilter(request, response);
        } catch (InvalidAccessTokenException e) {
            logger.error("Error validating access token", e);
            sendAuthenticationError(response);
        }
    }

    /**
     * Sends 401 Unauthorized response.
     */
    private void sendAuthenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }

    /**
     * Sets up the Spring Security context with user authentication details based on the provided access token.
     */
    private void setupSpringSecurityContext(AccessToken accessToken) {
        UserDetails userDetails = new User(accessToken.getSubject(), "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessToken.getRole())));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(accessToken);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
