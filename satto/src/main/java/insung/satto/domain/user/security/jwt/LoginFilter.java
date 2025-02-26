package insung.satto.domain.user.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.security.CustomUserDetails;
import insung.satto.domain.user.dto.HttpResponseUtil;
import insung.satto.domain.user.dto.JwtPair;
import insung.satto.domain.user.entity.RefreshEntity;
import insung.satto.domain.user.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    //JWTUtil 주입
    private final JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication()");

        //클라이언트 요청에서 studentId, password 추출
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);
            String studentId = requestBody.get("studentId");
            // 암호화 되기전 비밀번호
            String password = requestBody.get("password");
            log.info("studentId = {}", studentId);
            log.info("password = {}", password);

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
        log.info("createJwt()");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String studentId = customUserDetails.getStudentId();
        log.info("studentId = {}", studentId);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();
        log.info("role = {}", role);

        // 토큰 생성
        String access = jwtUtil.createJwt("access", studentId, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", studentId, role, 86400000L);

        // Refresh 토큰 db 저장
        addRefreshEntity(studentId, refresh, 86400000L);

        JwtPair jwtPair = new JwtPair(
                access,
                refresh
        );

        HttpResponseUtil.setSuccessResponse(response, HttpStatus.CREATED, jwtPair);
    }

    private void addRefreshEntity(String studentId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setStudentId(studentId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("unsuccessfulAuthentication()");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiResponse<?> responseBody = ApiResponse.onFailure("401", "아이디 혹은 비밀번호가 일치하지 않습니다");
        response.getWriter().write(responseBody.toJsonString());
    }
}
