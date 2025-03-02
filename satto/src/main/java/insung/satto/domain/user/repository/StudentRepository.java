package insung.satto.domain.user.repository;

import insung.satto.domain.user.entity.Student;

public interface StudentRepository {

    Student save(Student student);

    Student findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    void changePublicStatus(boolean nowStatus, String studentId);

    void withdrawal(String studentId);

    void changePassword(String password, String studentId);

    void editProfile(String studentId, String username, String nickname, String department, Integer grade);

}

