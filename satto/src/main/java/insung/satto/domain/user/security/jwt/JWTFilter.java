package insung.satto.domain.user.security.jwt;

import insung.satto.domain.user.security.CustomUserDetails;
import insung.satto.domain.user.entity.Student;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

        if(accessToken != null) {
            accessToken = accessToken.substring(7);
        }

        log.info("accessToken = {}", accessToken);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);
            log.info("토큰 없음");

            return;
        }
//        accessToken.substring(7);

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.info("토큰 만료");
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            // 클라랑 소통해서 상태메시지 통일해야함
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        log.info("category = {}", category);

        if (!category.equals("access")) {
            log.info("access 토큰이 아님");
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// username, role 값을 획득
//        String username = jwtUtil.getUsername(accessToken);
//        String role = jwtUtil.getRole(accessToken);

        String studentId = jwtUtil.getStudentId(accessToken);
        String role = "ROLE_" + jwtUtil.getRole(accessToken);

        log.info("studentId = {}", studentId);
        log.info("role = {}", role);

        Student student = new Student();
        student.setStudentId(studentId);
        student.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(student);

        System.out.println(customUserDetails);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println(authToken);

        filterChain.doFilter(request, response);
        log.info("필터체인");
    }
}
