package insung.satto.domain.user.controller;

import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.JwtPair;
import insung.satto.domain.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

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
