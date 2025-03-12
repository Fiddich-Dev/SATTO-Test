package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryTest {

    User user = new User("201910914", "password", "황인성", "insung", "컴퓨터과학과", 3, true, "STUDENT", "프로필이미지경로");

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void save() {
        // given
        User user = this.user;

        // when
        User savedUser = userRepository.save(user);

        // then
        User findUser = userRepository.findById(user.getId()).get();
        assertThat(savedUser.getStudentId()).isEqualTo(findUser.getStudentId());
    }

    @Test
    void findById() {
        // given
        User user = this.user;
        User savedUser = userRepository.save(user);

        // when
        User findUser = userRepository.findById(savedUser.getId()).get();

        // then
        assertThat(savedUser).isEqualTo(findUser);
    }

    @Test
    void existsByStudentId() {

        // given
        User user = this.user;
        User savedUser = userRepository.save(user);

        // when
        boolean isExist = userRepository.existsByStudentId(savedUser.getStudentId());
        boolean isExist2 = userRepository.existsByStudentId("없는 학번");

        // then
        assertThat(isExist).isTrue();
        assertThat(isExist2).isFalse();

    }

    @Test
    void toggleAccountPrivacy() {
        // given
        User user = this.user;
        userRepository.save(user);

        // when
        userRepository.toggleAccountPrivacy(user.getId());

        // then
        User findUser = userRepository.findById(user.getId()).get();
        assertThat(findUser.getIsPublic()).isFalse();
        assertThat(findUser.getIsPublic()).isEqualTo(user.getIsPublic());

    }

    @Test
    void withdrwal() {
        // given
        User user = this.user;
        userRepository.save(user);

        // when
        userRepository.withdrawal(user.getId());

        // then
        Optional<User> findUser = userRepository.findById(user.getId());
        assertThat(findUser).isEmpty();
    }

    @Test
    void changePassword() {
        // given
        User user = this.user;
        userRepository.save(user);

        // when
        userRepository.changePassword("1234", user.getId());

        // then
        User savedUser = userRepository.findById(user.getId()).get();
        assertThat(savedUser.getPassword()).isEqualTo("1234");
        assertThat(user.getPassword()).isEqualTo("1234");

    }

    @Test
    void editProfile() {
        // given
        User user = this.user;
        userRepository.save(user);

        // when
        EditProfileDTO editProfileDTO = new EditProfileDTO();
        editProfileDTO.setUsername("new황인성");
        editProfileDTO.setNickname("newinsung");
        editProfileDTO.setDepartment("new컴퓨터과학과");
        editProfileDTO.setGrade(5);
        userRepository.editProfile(user.getId(), editProfileDTO);

        // then
        User savedUser = userRepository.findById(user.getId()).get();
        assertThat(savedUser.getUsername()).isEqualTo(editProfileDTO.getUsername());
        assertThat(savedUser.getNickname()).isEqualTo(editProfileDTO.getNickname());
        assertThat(savedUser.getDepartment()).isEqualTo(editProfileDTO.getDepartment());
        assertThat(savedUser.getGrade()).isEqualTo(editProfileDTO.getGrade());

        assertThat(user.getUsername()).isEqualTo(editProfileDTO.getUsername());
        assertThat(user.getNickname()).isEqualTo(editProfileDTO.getNickname());
        assertThat(user.getDepartment()).isEqualTo(editProfileDTO.getDepartment());
        assertThat(user.getGrade()).isEqualTo(editProfileDTO.getGrade());

    }

    @Test
    void editProfileImage() {
        // given
        User user = this.user;
        userRepository.save(user);

        // when
        userRepository.editProfileImage(user.getId(), "newImage.png");

        // then
        User savedUser = userRepository.findById(user.getId()).get();
        assertThat(savedUser.getProfileImage()).isEqualTo("newImage.png");
        assertThat(user.getProfileImage()).isEqualTo("newImage.png");
    }


}