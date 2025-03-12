package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    void save(User user);

    User findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    void toggleAccountPrivacy(@Param("currentStatus") boolean currentStatus, @Param("studentId") String studentId);

    void withdrawal(String studentId);

    void changePassword(@Param("password") String password, @Param("studentId") String studentId);

    void editProfile(@Param("studentId") String studentId, @Param("editProfileDTO") EditProfileDTO editProfileDTO);

    void editProfileImage(@Param("studentId") String studentId, @Param("profileImage") String profileImage);

}
