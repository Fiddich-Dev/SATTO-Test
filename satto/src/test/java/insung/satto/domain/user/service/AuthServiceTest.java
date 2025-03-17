package insung.satto.domain.user.service;

import insung.satto.domain.user.dto.JoinDTO;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.QuerydslUserRepository;
import insung.satto.domain.user.repository.SpringDataJpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    QuerydslUserRepository querydslUserRepository;

    @Autowired
    SpringDataJpaUserRepository springDataJpaUserRepository;

    JoinDTO joinDto = new JoinDTO("201910914", "password", "황인성", "insung", "컴퓨터과학과", 3, true);

    User findById(Long id) {
        return springDataJpaUserRepository.findById(id).orElseThrow();
    }


    @Test
    void joinProcess() {

        // given
        JoinDTO joinDto = this.joinDto;

        // when
        User user = authService.joinProcess(joinDto);

        // then
        User findUser = findById(user.getId());
        assertThat(user).isEqualTo(findUser);
    }

    @Test
    void duplicateJoinProcess() {

        // given
        JoinDTO joinDto = this.joinDto;

        // when
        User user1 = authService.joinProcess(joinDto);

        // then
        assertThrows(DuplicateKeyException.class, () -> {
            authService.joinProcess(joinDto);
        });
    }
  
}