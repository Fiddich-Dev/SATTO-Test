package insung.satto.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.UserRepository;
import insung.satto.domain.user.security.jwt.JWTUtil;
import insung.satto.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Controller
@ResponseBody
public class UserController {

    @Value("${file.dir}")
    private String fileDir;

    private final UserService userService;
    private final JWTUtil jwtUtill;

    public UserController(UserService userService, JWTUtil jwtUtill) {
        this.userService = userService;
        this.jwtUtill = jwtUtill;
    }

    @PostMapping("/checkDuplicatedStudentId")
    public ApiResponse<?> existsByStudentId(@RequestBody Map<String, String> request) {
        log.info("checkDuplicatedStudentId()");
        String studentId = request.get("studentId");
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
        Long id = jwtUtill.getId(accessToken);
        User user = userService.findById(id).get();
        ApiResponse<?> response = ApiResponse.onSuccess(user);
        return response;
    }

    @PatchMapping("/changePublicStatus")
    public ApiResponse<?> changePublicStatus(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        Long id = jwtUtill.getId(accessToken);
//        try {
//            userService.changePublicStatus(studentId);
//            return ApiResponse.onSuccess(null);
//        } catch (DuplicateKeyException e) {
//            return ApiResponse.onFailure("403", e.getMessage());
//        }
        userService.changePublicStatus(id);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/withdrawal")
    public ApiResponse<?> withdrawal(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        Long id = jwtUtill.getId(accessToken);
        try {
            userService.withdrawal(id);
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
        Long id = jwtUtill.getId(accessToken);
        if(userService.verifyCurrentPassword(id, password)) {
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
        Long id = jwtUtill.getId(accessToken);
        String studentId = jwtUtill.getStudentId(accessToken);
        userService.changePassword(id, password);
        return ApiResponse.onSuccess(null);
    }



    @PostMapping("/editProfile")
    public ApiResponse<?> editProfile(@RequestHeader("Authorization") String accessToken, @RequestBody EditProfileDTO editProfileDTO) {
        log.info("editProfile()");
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        Long id = jwtUtill.getId(accessToken);

        try {
            userService.editProfile(id, editProfileDTO);
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
        Long id = Long.valueOf(request.get("id"));
        try {
            userService.sendNewPassword(id);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("402", "에러");
        }
    }

    @PostMapping("/profileImage")
    public ApiResponse<?> uploadProfileImage(@RequestHeader("Authorization") String accessToken, @RequestParam("file") MultipartFile profileImage) {

        log.info("uploadProfileImage()");
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        Long id = jwtUtill.getId(accessToken);

        if(profileImage.isEmpty()) {
            return ApiResponse.onFailure("400", "파일이 비어있습니다");
        }

        try {
            String imageUrl = userService.uploadProfileImage(id, profileImage);
            return ApiResponse.onSuccess(imageUrl);
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패", e);
            return ApiResponse.onFailure("400", "이미지 업로드 실패");
        }
    }

    @DeleteMapping("/profileImage")
    public ApiResponse<?> deleteProfileImage(@RequestHeader("Authorization") String accessToken) {
        log.info("deleteProfileImage()");
        accessToken = accessToken.substring(7);
        String studentId = jwtUtill.getStudentId(accessToken);
        Long id = jwtUtill.getId(accessToken);
        try {
            userService.deleteProfileImage(id);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("400", e.getMessage());
        }

    }
}
