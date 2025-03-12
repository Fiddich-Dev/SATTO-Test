package insung.satto.domain.user.service;

import insung.satto.domain.user.dto.JoinDTO;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;


    @Test
    void joinProcess() {

        JoinDTO joinDTO = new JoinDTO("201910914",
                bCryptPasswordEncoder.encode("1234"),
                "황인성",
                "insung",
                "컴퓨터과학과",
                3,
                true);

        User joinedUser = authService.joinProcess(joinDTO);

        User findUser = userRepository.findById(joinedUser.getId()).get();

        assertThat(joinedUser).isEqualTo(findUser);

        assertThrows(DuplicateKeyException.class, () -> {
            authService.joinProcess(joinDTO);
        });
    }

    @Test
    void login() {
        String studentId = "201910914";
        String password = "1234";


    }

    @Test
    void reissueProcess() {
        JoinDTO joinDTO = new JoinDTO("201910914",
                bCryptPasswordEncoder.encode("1234"),
                "황인성",
                "insung",
                "컴퓨터과학과",
                3,
                true);

        authService.joinProcess(joinDTO);


    }




}