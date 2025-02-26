package insung.satto.domain.user.repository;

import insung.satto.domain.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Student, Integer> {

//    boolean existsByUsername(String username);

    Student findByUsername(String username);
}
