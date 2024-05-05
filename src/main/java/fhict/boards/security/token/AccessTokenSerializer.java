package fhict.boards.security.token;

public interface AccessTokenSerializer {
    AccessToken decode(String accessTokenEncoded);
    String encode(AccessToken accessToken);
}
