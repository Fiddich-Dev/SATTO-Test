package insung.satto.domain.user.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.security.CustomUserDetails;
import insung.satto.domain.user.dto.HttpResponseUtil;
import insung.satto.domain.user.dto.JwtPair;
import insung.satto.domain.user.entity.RefreshEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication()");

        //클라이언트 요청에서 studentId, password 추출
        // Java 객체를 JSON 문자열로 변환 (Serialization), 
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);
            String studentId = requestBody.get("studentId");
            // 암호화 되기전 비밀번호
            String password = requestBody.get("password");

            //스프링 시큐리티에서 studentId와 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(studentId, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // jwtFilter 마지막에 컨텍스트 홀더에 저장한 값을 가져온다
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 학번과 권한을 가져온다
        String studentId = customUserDetails.getStudentId();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();
        Long id = customUserDetails.getId();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", id, studentId, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", id, studentId, role, 86400000L);

        // redis에 refresh토큰만 저장
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(studentId + ":refreshToken", refresh);
        redisTemplate.expire(studentId + ":refreshToken", Duration.ofDays(7));

        JwtPair jwtPair = new JwtPair(
                access,
                refresh
        );

        HttpResponseUtil.setSuccessResponse(response, HttpStatus.CREATED, jwtPair);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("unsuccessfulAuthentication()");
        HttpResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiResponse.onFailure(HttpStatus.BAD_REQUEST.name(), "아이디 혹은 비밀번호가 일치하지 않습니다"));
    }
}
