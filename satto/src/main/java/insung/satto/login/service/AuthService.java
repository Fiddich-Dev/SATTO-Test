package insung.satto.login.service;


import insung.satto.login.dto.JwtPair;
import insung.satto.login.entity.RefreshEntity;
import insung.satto.login.jwt.JWTUtil;
import insung.satto.login.repository.RefreshRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;

    public AuthService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public JwtPair reissueProcess(String refreshToken) {

        log.info("refreshToken = {}", refreshToken);
        // 토큰이 비어있는지 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        // 토큰이 db에 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            throw new NoSuchElementException("Refresh token not found");
        }

        String studentId = jwtUtil.getStudentId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //make new JWT
        String newAccessToken = jwtUtil.createJwt("access", studentId, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", studentId, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(studentId, newRefreshToken, 86400000L);

        return new JwtPair(newAccessToken, newRefreshToken);
    }

    private void addRefreshEntity(String studentId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setStudentId(studentId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
