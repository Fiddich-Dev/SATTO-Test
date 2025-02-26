package insung.satto.domain.user.controller;

import insung.satto.domain.user.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@Controller
@ResponseBody
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestParam String email) {
        String authCode = mailService.generateAuthCode();
        mailService.sendAuthCode(email, authCode);


        // 실제로는 인증번호를 Redis 같은 곳에 저장해야 함
        mailService.saveAuthCode(email, authCode);

        
        return "인증번호가 이메일로 전송되었습니다.";
    }

    @PostMapping("/verify")
    public String verifyAuthCode(@RequestParam String email, @RequestParam String code) {
        log.info("verifyAuthCode()");
        boolean isValid = mailService.verifyAuthCode(email, code);
        return isValid ? "인증 성공" : "인증 실패";
    }
}
