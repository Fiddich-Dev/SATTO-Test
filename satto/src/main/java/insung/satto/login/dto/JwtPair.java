package insung.satto.login.dto;

public record JwtPair(
        String accessToken,
        String refreshToken
) {
}
