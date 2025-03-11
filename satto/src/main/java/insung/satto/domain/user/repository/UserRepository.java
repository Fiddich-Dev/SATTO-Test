package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;

public interface UserRepository {

    User save(User user);

    User findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    void toggleAccountPrivacy(boolean currentStatus, String studentId);

    void withdrawal(String studentId);

    void changePassword(String password, String studentId);

    void editProfile(String studentId, EditProfileDTO editProfileDTO);

    void editProfileImage(String studentId, String profileImage);

}

