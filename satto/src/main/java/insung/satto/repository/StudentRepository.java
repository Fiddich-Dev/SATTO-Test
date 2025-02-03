package insung.satto.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StudentRepository {

    private static final Map<String, Student> store = new HashMap<>();

    public Student save(Student student) {
        store.put(student.getStudentId(), student);
        return student;
    }

    public Student findByStudentId(String studentId) {
        return store.get(studentId);
    }

    public Student update(String studentId, Student student) {
        Student findStudent = findByStudentId(studentId);
        findStudent.setPassword(student.getPassword());
        findStudent.setName(student.getName());
        findStudent.setNickname(student.getNickname());
        findStudent.setDepartment(student.getDepartment());
        findStudent.setAge(student.getAge());
        findStudent.setIsPublic(student.getIsPublic());
        return student;
    }

    public void clearStore() {
        store.clear();
    }
}
