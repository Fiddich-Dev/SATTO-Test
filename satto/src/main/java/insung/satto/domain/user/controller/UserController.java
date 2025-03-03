package insung.satto.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.UserRepository;
import insung.satto.domain.user.security.jwt.JWTUtil;
import insung.satto.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@ResponseBody
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtill;

    public UserController(UserService userService, UserRepository userRepository, JWTUtil jwtUtill) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtill = jwtUtill;
    }

    @PostMapping("/checkDuplicatedStudentId")
    public ApiResponse<?> existsByStudentId(@RequestBody Map<String, String> request) {
        log.info("checkDuplicatedStudentId()");
        String studentId = request.get("studentId");
        log.info(studentId);
        if(userService.existsByStudentId(studentId)) {
            return ApiResponse.onFailure("403", "학번이 이미 존재합니다");
        }
        else {
            return ApiResponse.onSuccess(null);
        }
    }

    @GetMapping("/info")
    public ApiResponse<?> findByStudentId(@RequestHeader("Authorization") String accessToken) {
        log.info("getMyAccountInfo()");
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        User user = userService.findByStudentId(studentId);
        ApiResponse<?> response = ApiResponse.onSuccess(user);
        return response;
    }

    @PatchMapping("/changePublicStatus")
    public ApiResponse<?> changePublicStatus(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        try {
            userService.changePublicStatus(studentId);
            return ApiResponse.onSuccess(null);
        } catch (DuplicateKeyException e) {
            return ApiResponse.onFailure("403", e.getMessage());
        }
    }

    @DeleteMapping("/withdrawal")
    public ApiResponse<?> withdrawal(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        try {
            userService.withdrawal(studentId);
            return ApiResponse.onSuccess(null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
    @PostMapping("/verifyCurrentPassword")
    public ApiResponse<?> verifyCurrentPassword(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> inputPassword) {
        accessToken = accessToken.substring(7);
        String password = inputPassword.get("password");
        String studentId = jwtUtill.getStudentId(accessToken);
        if(userService.verifyCurrentPassword(studentId, password)) {
            return ApiResponse.onSuccess(null);
        }
        else {
            return ApiResponse.onFailure("401", "비밀번호가 일치하지 않음");
        }
    }

    // 바꾸는 비번이 현재 비번과 같으면 안됨 구현해야함
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> inputPassword) {
        accessToken = accessToken.substring(7);
        String password = inputPassword.get("password");
        String studentId = jwtUtill.getStudentId(accessToken);
        userService.changePassword(studentId, password);
        return ApiResponse.onSuccess(null);
    }



    @PostMapping("/editProfile")
    public ApiResponse<?> editProfile(@RequestHeader("Authorization") String accessToken, @RequestBody EditProfileDTO editProfileDTO) {
        log.info("editProfile()");
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);

        try {
            userService.editProfile(studentId, editProfileDTO);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("403", "에러발생");
        }
    }

    @PostMapping("/sendNewPassword")
    public ApiResponse<?> sendNewPassword(@RequestBody Map<String, String> request) {
        log.info("sendNewPassword()");
        String studentId = request.get("studentId");
        try {
            userService.sendNewPassword(studentId);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("402", "에러");
        }
    }
}
