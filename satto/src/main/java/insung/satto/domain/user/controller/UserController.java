package insung.satto.domain.user.controller;

import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.ChangePasswordDto;
import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.dto.StudenIdDTO;
import insung.satto.domain.user.entity.Student;
import insung.satto.domain.user.repository.StudentRepository;
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

    UserService userService;
    StudentRepository studentRepository;

    public UserController(UserService userService, StudentRepository studentRepository) {
        this.userService = userService;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/checkDuplicatedStudentId")
    public ApiResponse<?> checkDuplicatedStudentId(@RequestBody Map<String, String> request) {
        log.info("checkDuplicatedStudentId()");
        String studentId = request.get("studentId");
        log.info(studentId);
        if(userService.checkDuplicatedStudentId(studentId)) {
            return ApiResponse.onFailure("403", "학번이 이미 존재합니다");
        }
        else {
            return ApiResponse.onSuccess(null);
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

    @PatchMapping("/changePublicStatus")
    public ApiResponse<?> changePublicStatus(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        Student student = userService.getMyAccountInfoProcess(accessToken);
        try {
            userService.changePublicStatus(student.getIsPublic(), student.getStudentId());
            return ApiResponse.onSuccess(null);
        } catch (DuplicateKeyException e) {
            return ApiResponse.onFailure("403", e.getMessage());
        }
    }

    @DeleteMapping("/withdrawal")
    public ApiResponse<?> withdrawal(@RequestHeader("Authorization") String accessToken) {
        accessToken = accessToken.substring(7);
        Student student = userService.getMyAccountInfoProcess(accessToken);
        try {
            userService.withdrawal(student.getStudentId());
            return ApiResponse.onSuccess(null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }


    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestHeader("Authorization") String accessToken, @RequestBody ChangePasswordDto changePasswordDto) {
        log.info("changePassword() = {}", accessToken);
        accessToken = accessToken.substring(7);
        Student student = userService.getMyAccountInfoProcess(accessToken);
        try {
            userService.changePassword(student, changePasswordDto);
            return ApiResponse.onSuccess(null);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/sendNewPassword")
    public ApiResponse<?> sendNewPassword(@RequestBody StudenIdDTO studenIdDTO) {
        log.info("sendNewPassword()");
        try {
            userService.sendNewPassword(studenIdDTO.getStudentId());
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("402", "에러");
        }
    }

    @PostMapping("/editProfile")
    public ApiResponse<?> editProfile(@RequestHeader("Authorization") String accessToken, @RequestBody EditProfileDTO editProfileDTO) {
        log.info("editProfile()");
        accessToken = accessToken.substring(7);
        Student student = userService.getMyAccountInfoProcess(accessToken);

        try {
            userService.editProfile(student.getStudentId(), editProfileDTO);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.onFailure("403", "에러발생");
        }
    }
}
