package insung.satto.domain.user.dto;

public record JwtPair(
        String accessToken,
        String refreshToken
) {
}
