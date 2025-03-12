package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserRepositoryMybatis implements UserRepository{

    private final UserMapper userMapper;

    public UserRepositoryMybatis(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        log.info("save()");
        userMapper.save(user);
        return user;
    }

    @Override
    public User findByStudentId(String studentId) {
        return userMapper.findByStudentId(studentId);
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return userMapper.existsByStudentId(studentId);
    }

    @Override
    public void toggleAccountPrivacy(boolean currentStatus, String studentId) {
        userMapper.toggleAccountPrivacy(!currentStatus, studentId);
    }

    @Override
    public void withdrawal(String studentId) {
        userMapper.withdrawal(studentId);
    }

    @Override
    public void changePassword(String password, String studentId) {
        userMapper.changePassword(password, studentId);
    }

    @Override
    public void editProfile(String studentId, EditProfileDTO editProfileDTO) {
        userMapper.editProfile(studentId, editProfileDTO);
    }

    @Override
    public void editProfileImage(String studentId, String profileImage) {
        userMapper.editProfileImage(studentId, profileImage);
    }
}
