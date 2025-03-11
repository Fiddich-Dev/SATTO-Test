package insung.satto.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String studentId;
    private String password;
    private String username;
    private String nickname;
    private String department;
    private Integer grade;
    private Boolean isPublic;
    private String profileImage;

    private String role;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(studentId, user.studentId) && Objects.equals(password, user.password) && Objects.equals(username, user.username) && Objects.equals(nickname, user.nickname) && Objects.equals(department, user.department) && Objects.equals(grade, user.grade) && Objects.equals(isPublic, user.isPublic) && Objects.equals(profileImage, user.profileImage) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, password, username, nickname, department, grade, isPublic, profileImage, role);
    }
}

