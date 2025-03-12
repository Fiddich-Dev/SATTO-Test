package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    boolean existsByStudentId(String studentId);

    void toggleAccountPrivacy(Long id);

    void withdrawal(Long id);

    void changePassword(String password, Long id);

    void editProfile(Long id, EditProfileDTO editProfileDTO);

    void editProfileImage(Long id, String profileImage);

}

