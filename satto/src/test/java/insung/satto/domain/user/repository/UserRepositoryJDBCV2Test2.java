package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryJDBCV2Test2 {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void save() {
        // give
        User user = new User("stdudentId", "password", "username");

        // when
        User savedUser = userRepository.save(user);

        // then
        User findUser = userRepository.findByStudentId(user.getStudentId());
        assertThat(savedUser.getStudentId()).isEqualTo(findUser.getStudentId());
    }

    @Test
    @DisplayName("유저 찾기")
    void findByStudentId() {
        // given
        User user = new User("stdudentId", "password", "username");
        User savedUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByStudentId("stdudentId");

        // then
        assertThat(savedUser).isEqualTo(findUser);
    }

    @Test
    void existsByStudentId() {

        assertThat(userRepository.existsByStudentId("201910914")).isFalse();

        // given
        User user = new User();
        user.setStudentId("201910914");
        user.setPassword("securePassword");
        user.setUsername("홍길동");
        user.setNickname("길동이");
        user.setDepartment("컴퓨터공학과");
        user.setGrade(3);
        user.setIsPublic(true);
        user.setRole("STUDENT");
        user.setProfileImage("profile_20240001.png");
        User savedUser = userRepository.save(user);

        assertThat(userRepository.existsByStudentId("201910914")).isTrue();

    }

    @Test
    void toggleAccountPrivacy() {
        // given
        User user = new User();
        user.setStudentId("201910914");
        user.setPassword("securePassword");
        user.setUsername("홍길동");
        user.setNickname("길동이");
        user.setDepartment("컴퓨터공학과");
        user.setGrade(3);
        user.setIsPublic(true);
        user.setRole("STUDENT");
        user.setProfileImage("profile_20240001.png");
        User savedUser = userRepository.save(user);

        // when
        userRepository.toggleAccountPrivacy(user.getIsPublic(), user.getStudentId());

        // then
        assertThat(user.getIsPublic()).isEqualTo(!userRepository.findByStudentId(user.getStudentId()).getIsPublic());
    }

    @Test
    void withdrwal() {
        // given
        User user = new User("stdudentId", "password", "username");
        User savedUser = userRepository.save(user);

        // when
        userRepository.withdrawal(user.getStudentId());

        // then
        assertThat(userRepository.existsByStudentId(user.getStudentId())).isFalse();
    }

    @Test
    void changePassword() {
        // given
        User user = new User();
        user.setStudentId("201910914");
        user.setPassword("securePassword");
        user.setUsername("홍길동");
        user.setNickname("길동이");
        user.setDepartment("컴퓨터공학과");
        user.setGrade(3);
        user.setIsPublic(true);
        user.setRole("STUDENT");
        user.setProfileImage("profile_20240001.png");
        User savedUser = userRepository.save(user);

        // when
        userRepository.changePassword("1234", user.getStudentId());

        // then
        assertThat(userRepository.findByStudentId(user.getStudentId()).getPassword()).isEqualTo("1234");


    }

    @Test
    void editProfile() {
        // given
        User user = new User();
        user.setStudentId("201910914");
        user.setPassword("securePassword");
        user.setUsername("홍길동");
        user.setNickname("길동이");
        user.setDepartment("컴퓨터공학과");
        user.setGrade(3);
        user.setIsPublic(true);
        user.setRole("STUDENT");
        user.setProfileImage("profile_20240001.png");
        User savedUser = userRepository.save(user);

        // when
        EditProfileDTO editProfileDTO = new EditProfileDTO();
        editProfileDTO.setUsername("황인성");
        editProfileDTO.setNickname("insung");
        editProfileDTO.setDepartment("컴퓨터과학과");
        editProfileDTO.setGrade(5);
        userRepository.editProfile(user.getStudentId(), editProfileDTO);

        // then
        User editedUser = userRepository.findByStudentId(user.getStudentId());
        assertThat(editedUser.getUsername()).isEqualTo(editProfileDTO.getUsername());
        assertThat(editedUser.getNickname()).isEqualTo(editProfileDTO.getNickname());
        assertThat(editedUser.getDepartment()).isEqualTo(editProfileDTO.getDepartment());
        assertThat(editedUser.getGrade()).isEqualTo(editProfileDTO.getGrade());



    }

    @Test
    void editProfileImage() {
        // given
        User user = new User();
        user.setStudentId("201910914");
        user.setPassword("securePassword");
        user.setUsername("홍길동");
        user.setNickname("길동이");
        user.setDepartment("컴퓨터공학과");
        user.setGrade(3);
        user.setIsPublic(true);
        user.setRole("STUDENT");
        user.setProfileImage("profile_20240001.png");
        User savedUser = userRepository.save(user);

        userRepository.editProfileImage(user.getStudentId(), "newImage.png");

        assertThat(userRepository.findByStudentId(user.getStudentId()).getProfileImage()).isEqualTo("newImage.png");
    }


}