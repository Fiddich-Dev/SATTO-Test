package insung.satto.login.repository;

import insung.satto.login.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Student, Integer> {

//    boolean existsByUsername(String username);

    Student findByUsername(String username);
}
