package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {

    // 쿼리 메서드
    boolean existsByStudentId(String studentId);

//    // 쿼리 직접 실행
//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.studentId = :studentId")
//    boolean existsByStudentId(@Param("studentId") String studentId);
}
