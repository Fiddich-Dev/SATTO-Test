package insung.satto.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Student {

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

    private String role;

}

