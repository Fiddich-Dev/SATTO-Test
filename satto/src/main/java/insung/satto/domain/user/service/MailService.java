package insung.satto.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> verificationCodes = new HashMap<>();
    private final RedisTemplate<String, String> redisTemplate;

    // 인증번호 생성 메소드
    public String generateAuthCode() {
        Random random = new Random();
        int authCode = 100000 + random.nextInt(900000); // 6자리 난수 생성
        return String.valueOf(authCode);
    }

    // 이메일 전송 메소드
    public void sendAuthCode(String toEmail, String authCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("이메일 인증 코드");
            helper.setText("<h3>인증 코드: <strong>" + authCode + "</strong></h3>", true); // HTML 형식

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }



    // 인증번호 저장 (이메일을 key로 사용)
    public void saveAuthCode(String email, String authCode) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, authCode, 3, TimeUnit.MINUTES);
        // 여러번 보내면 대치되도록
//        verificationCodes.put(email, authCode);
    }

    // 인증번호 검증
    public boolean verifyAuthCode(String email, String inputCode) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String storedCode = valueOperations.getAndDelete(email); // email(key)에 해당하는 값(authCode) 가져오기
        return storedCode != null && storedCode.equals(inputCode);



//        String storedCode = verificationCodes.get(email);
//        return storedCode != null && storedCode.equals(inputCode);
    }
}