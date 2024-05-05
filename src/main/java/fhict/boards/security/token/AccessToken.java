package fhict.boards.security.token;

import fhict.boards.domain.enums.AccessRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@Builder
public class AccessToken {
    private final String subject;
    private final Long userId;
    private final AccessRole role;
}