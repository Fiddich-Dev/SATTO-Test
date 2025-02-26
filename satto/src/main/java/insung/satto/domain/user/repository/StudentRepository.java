package insung.satto.domain.user.repository;

import insung.satto.domain.user.entity.Student;

public interface StudentRepository {

    Student save(Student student);

    Student findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

}

