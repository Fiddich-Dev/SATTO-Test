package insung.satto.login.controller;

import insung.satto.login.dto.ApiResponse;
import insung.satto.login.dto.JwtPair;
import insung.satto.login.entity.RefreshEntity;
import insung.satto.login.jwt.JWTUtil;
import insung.satto.login.repository.RefreshRepository;
import insung.satto.login.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@ResponseBody
@Slf4j
public class ReissueController {

    private final AuthService authService;

    public ReissueController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/reissue")
    public ApiResponse<?> reissue(@RequestHeader("refreshToken") String refreshToken) {

        try {
            JwtPair newJwtpair = authService.reissueProcess(refreshToken);
            ApiResponse<JwtPair> reponse = ApiResponse.onSuccess(newJwtpair);
            return reponse;
        } catch (RuntimeException e) {
            log.info("토큰 오류발생", e);
            ApiResponse<?> response = ApiResponse.onFailure("403", "잘못된 토큰입니다");
            return response;
        }
    }

}
