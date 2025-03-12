package insung.satto.domain.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDTO {
    private String username;
    private String nickname;
    private String department;
    private Integer grade;
}
