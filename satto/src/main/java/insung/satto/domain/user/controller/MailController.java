package insung.satto.domain.user.controller;

import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@ResponseBody
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ApiResponse<?> sendEmail(@RequestParam(value = "email") String email) {
        log.info("sendEmail()");
        mailService.sendAuthCode(email);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/verify")
    public ApiResponse<?> verifyAuthCode(@RequestParam String email, @RequestParam String code) {
        log.info("verifyAuthCode()");
        boolean isValid = mailService.verifyAuthCode(email, code);
        if(isValid) {
            return ApiResponse.onSuccess(null);
        }
        else {
            return ApiResponse.onFailure("403", "인증실패");
        }
    }

}
