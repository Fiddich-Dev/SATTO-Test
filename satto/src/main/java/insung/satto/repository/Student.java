package insung.satto.repository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

    String studentId;
    String password;
    String name;
    String nickname;
    String department;
    Integer age;
    Boolean isPublic;

    public Student() {
    }

    public Student(String studentId, String password, String name, String nickname, String department, Integer age, Boolean isPublic) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.department = department;
        this.age = age;
        this.isPublic = isPublic;
    }
}