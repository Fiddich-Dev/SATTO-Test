package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import io.lettuce.core.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositorySpringDataJPA implements UserRepository{

    private final SpringDataJpaUserRepository repository;

    @Override
    public User save(User user) {
        log.info("save()");
        return repository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return repository.existsByStudentId(studentId);
    }

    @Override
    public void toggleAccountPrivacy(Long id) {
        User findUser = repository.findById(id).orElseThrow();
        findUser.setIsPublic(!findUser.getIsPublic());
    }

    @Override
    public void withdrawal(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void changePassword(String password, Long id) {
        User findUser = repository.findById(id).orElseThrow();
        findUser.setPassword(password);
    }

    @Override
    public void editProfile(Long id, EditProfileDTO editProfileDTO) {
        User findUser = repository.findById(id).orElseThrow();
        findUser.setUsername(editProfileDTO.getUsername());
        findUser.setNickname(editProfileDTO.getNickname());
        findUser.setDepartment(editProfileDTO.getDepartment());
        findUser.setGrade(editProfileDTO.getGrade());
    }

    @Override
    public void editProfileImage(Long id, String profileImage) {
        User findUser = repository.findById(id).orElseThrow();
        findUser.setProfileImage(profileImage);
    }
}
