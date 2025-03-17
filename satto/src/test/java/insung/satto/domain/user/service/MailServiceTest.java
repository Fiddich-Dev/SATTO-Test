package insung.satto.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class MailServiceTest {

    @Autowired
    MailService mailService;

//    @Test
//    void authMailLogic() {
//        String email = "201910914@sangmyung.kr";
//        String authcode  = mailService.sendAuthCode(email);
//        assertThat(mailService.verifyAuthCode(email, authcode)).isTrue();
//        assertThat(mailService.verifyAuthCode(email, "틀린코드")).isFalse();
//    }

}