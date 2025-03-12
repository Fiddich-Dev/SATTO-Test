package insung.satto.domain.user.repository;


import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
//@Repository
@Transactional
public class UserRepositoryJPA implements UserRepository {

    private final EntityManager em;

    public UserRepositoryJPA(EntityManager em) {
        this.em = em;
    }


    @Override
    public User save(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public User findByStudentId(String studentId) {
        User findUser = em.find(User.class, studentId);
        return findUser;
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        boolean isExist = em.contains(studentId);
        return isExist;
    }

    @Override
    public void toggleAccountPrivacy(boolean currentStatus, String studentId) {
        User findUser = em.find(User.class, studentId);
        findUser.setIsPublic(!currentStatus);
    }

    @Override
    public void withdrawal(String studentId) {
        User findUser = em.find(User.class, studentId);
        em.remove(findUser);
    }

    @Override
    public void changePassword(String password, String studentId) {
        User findUser = em.find(User.class, studentId);
        findUser.setPassword(password);
    }

    @Override
    public void editProfile(String studentId, EditProfileDTO editProfileDTO) {
        User findUser = em.find(User.class, studentId);
        findUser.setUsername(editProfileDTO.getUsername());
        findUser.setNickname(editProfileDTO.getNickname());
        findUser.setDepartment(editProfileDTO.getDepartment());
        findUser.setGrade(editProfileDTO.getGrade());
    }

    @Override
    public void editProfileImage(String studentId, String profileImage) {
        User findUser = em.find(User.class, studentId);
        findUser.setProfileImage(profileImage);
    }
}
