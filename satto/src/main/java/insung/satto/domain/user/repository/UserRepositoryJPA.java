//package insung.satto.domain.user.repository;
//
//
//import insung.satto.domain.user.dto.EditProfileDTO;
//import insung.satto.domain.user.entity.User;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.parameters.P;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Repository
//@Transactional
//public class UserRepositoryJPA implements UserRepository {
//
//    private final EntityManager em;
//
//    public UserRepositoryJPA(EntityManager em) {
//        this.em = em;
//    }
//
//    @Override
//    public User save(User user) {
//        em.persist(user);
//        return user;
//    }
//
//    @Override
//    public Optional<User> findById(Long id) {
//        User findUser = em.find(User.class, id);
//        return Optional.ofNullable(findUser);
//    }
//
//    @Override
//    public List<User> findAll() {
//        String jpql = "select u from User u";
//        TypedQuery<User> query = em.createQuery(jpql, User.class);
//        List<User> users = query.getResultList();
//        return users;
//    }
//
//    @Override
//    public boolean existsByStudentId(String studentId) {
//        String jpql = "select count(*) from User u where u.studentId = :studentId";
//        Long findUsersCount = em.createQuery(jpql, Long.class).setParameter("studentId", studentId).getSingleResult();
//        return findUsersCount >= 1;
//    }
//
//    @Override
//    public void toggleAccountPrivacy(Long id) {
//        User findUser = em.find(User.class, id);
//        findUser.setIsPublic(!findUser.getIsPublic());
//    }
//
//    @Override
//    public void withdrawal(Long id) {
//        User findUser = em.find(User.class, id);
//        em.remove(findUser);
//    }
//
//    @Override
//    public void changePassword(String password, Long id) {
//        User findUser = em.find(User.class, id);
//        findUser.setPassword(password);
//    }
//
//    @Override
//    public void editProfile(Long id, EditProfileDTO editProfileDTO) {
//        User findUser = em.find(User.class, id);
//        findUser.setUsername(editProfileDTO.getUsername());
//        findUser.setNickname(editProfileDTO.getNickname());
//        findUser.setDepartment(editProfileDTO.getDepartment());
//        findUser.setGrade(editProfileDTO.getGrade());
//    }
//
//    @Override
//    public void editProfileImage(Long id, String profileImage) {
//        User findUser = em.find(User.class, id);
//        findUser.setProfileImage(profileImage);
//    }
//}
