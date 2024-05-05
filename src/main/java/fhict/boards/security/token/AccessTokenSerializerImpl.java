package fhict.boards.security.token;

import fhict.boards.exception.InvalidAccessTokenException;
import fhict.boards.domain.enums.AccessRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for encoding and decoding access tokens using JWTs.
 */
@Service
public class AccessTokenSerializerImpl implements AccessTokenSerializer {
    private final Key key;

    public AccessTokenSerializerImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Encodes an access token into a JWT with specified claims and signs it with a secret key.
     */
    @Override
    public String encode(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();

        if (accessToken.getRole() != null) {
            claimsMap.put("role", accessToken.getRole());
        }
        if (accessToken.getUserId() != null) {
            claimsMap.put("userId", accessToken.getUserId());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }

    /**
     * Decodes a JWT into an access token, extracting subject (email), roles, and user ID.
     */
    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            AccessRole role = AccessRole.valueOf(claims.get("role", String.class));
            Long userId = claims.get("userId", Long.class);

            return AccessToken.builder()
                    .subject(claims.getSubject())
                    .userId(userId)
                    .role(role)
                    .build();
        } catch (JwtException e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }
}
