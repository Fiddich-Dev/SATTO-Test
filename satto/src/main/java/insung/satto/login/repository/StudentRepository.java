package insung.satto.login.repository;

import insung.satto.login.entity.Student;

public interface StudentRepository {

    Student save(Student student);

    Student findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

}

