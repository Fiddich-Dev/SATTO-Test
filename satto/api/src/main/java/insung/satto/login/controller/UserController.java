package insung.satto.login.controller;

import insung.satto.login.dto.ApiResponse;
import insung.satto.login.dto.JoinDTO;
import insung.satto.login.entity.Student;
import insung.satto.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@ResponseBody
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ApiResponse<?> joinUser(@RequestBody JoinDTO joinDTO) {
        try {
            userService.joinProcess(joinDTO);
            ApiResponse<?> response = ApiResponse.onSuccess(null);
            return response;
        } catch (DuplicateKeyException e) {
            log.info("DuplicateKeyException", e);
            ApiResponse<?> response = ApiResponse.onFailure("403", "이미 가입된 학번입니다.");
            return response;
        }
    }

    @GetMapping("/info")
    public ApiResponse<?> getMyAccountInfo(@RequestHeader("Authorization") String accessToken) {
        log.info("getMyAccountInfo()");
        accessToken = accessToken.substring(7);
        log.info("accessToken = {}", accessToken);

        Student student = userService.getMyAccountInfoProcess(accessToken);
        ApiResponse<?> response = ApiResponse.onSuccess(student);
        return response;
    }


}
