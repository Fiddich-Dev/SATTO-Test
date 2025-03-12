package insung.satto.domain.user.controller;

import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.JoinDTO;
import insung.satto.domain.user.dto.JwtPair;
import insung.satto.domain.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@ResponseBody
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/join")
    public ApiResponse<?> joinProcess(@RequestBody JoinDTO joinDTO) {
        authService.joinProcess(joinDTO);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(@RequestHeader("refreshToken") String refreshToken) {
        return ApiResponse.onSuccess(authService.reissueProcess(refreshToken));
    }

}
