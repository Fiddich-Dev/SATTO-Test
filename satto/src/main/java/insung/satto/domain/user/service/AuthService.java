package insung.satto.domain.user.service;


import insung.satto.domain.user.dto.JoinDTO;
import insung.satto.domain.user.dto.JwtPair;
import insung.satto.domain.user.entity.RefreshEntity;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.UserRepository;
import insung.satto.domain.user.security.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthService(JWTUtil jwtUtil, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.redisTemplate = redisTemplate;
    }

    public User joinProcess(JoinDTO joinDTO) throws DuplicateKeyException {

        String studentId = joinDTO.getStudentId();
        String password = joinDTO.getPassword();
        String username = joinDTO.getUsername();
        String nickname = joinDTO.getNickname();
        String department = joinDTO.getDepartment();
        Integer grade = joinDTO.getGrade();
        Boolean isPublic = joinDTO.getIsPublic();

        // 학번이 안겹치는지 확인하는 로직
        if(userRepository.existsByStudentId(studentId)) {
            throw new DuplicateKeyException("이미 존재하는 학번입니다");
        }

        User data = new User();

        data.setStudentId(studentId);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setUsername(username);
        data.setNickname(nickname);
        data.setDepartment(department);
        data.setGrade(grade);
        data.setIsPublic(isPublic);
        data.setRole("USER");
        data.setProfileImage(null);

        userRepository.save(data);
        return data;
    }

    public JwtPair reissueProcess(String refreshToken) {

        // 토큰이 비어있는지 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        String studentId = jwtUtil.getStudentId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰이 redis에 있는지 확인
        List<String> refreshTokens = redisTemplate.opsForList().range(studentId + ":refreshToken", 0, -1);
        Boolean isExist = refreshTokens.contains(refreshToken);
        if (!isExist) {
            throw new NoSuchElementException("Refresh token not found");
        }

        // 새로운 access, refresh 토큰 재발급
        String newAccessToken = jwtUtil.createJwt("access", studentId, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", studentId, role, 86400000L);

        // redis 리이슈 하는데 사용한 refresh토큰 삭제
        // 새로 받은 refresh 토큰 redis에 저장
        // 만료기간 7일로 갱신
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.remove(studentId + ":refreshToken", 1, refreshToken);
        listOps.rightPush(studentId + ":refreshToken", newRefreshToken);
        redisTemplate.expire(studentId + ":refreshToken", Duration.ofDays(7));

        return new JwtPair(newAccessToken, newRefreshToken);
    }

}
