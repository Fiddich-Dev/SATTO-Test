package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
//@Repository
public class UserRepositoryJDBC implements UserRepository {

    private final JdbcTemplate template;

    public UserRepositoryJDBC(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public User save(User user) {
        String sql = "insert into users(studentId, password, username, nickname, department, grade, isPublic, role, profileImage) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        template.update(sql, user.getStudentId(), user.getPassword(), user.getUsername(), user.getNickname(), user.getDepartment(), user.getGrade(), user.getIsPublic(), user.getRole(), null);
        return user;
    }

    @Override
    public User findByStudentId(String studentId) {
        String sql = "select * from users where studentId = ?";
        User findUser = template.queryForObject(sql, studentRowMapper(), studentId);
        return findUser;
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        String sql = "select count(*) from users where studentId = ?";
        Integer count = template.queryForObject(sql, Integer.class, studentId);
        log.info("count = {}", count);
        return count == 1;
    }

    @Override
    public void toggleAccountPrivacy(boolean currentStatus, String studentId) {
        String sql = "update users set isPublic = ? where studentId = ?";
        template.update(sql, !currentStatus, studentId);
    }

    @Override
    public void withdrawal(String studentId) {
        String sql = "delete from users where studentId = ?";
        template.update(sql, studentId);
    }

    @Override
    public void changePassword(String password, String studentId) {
        String sql = "update users set password = ? where studentId = ?";
        template.update(sql, password, studentId);
    }

    @Override
    public void editProfile(String studentId, EditProfileDTO editProfileDTO) {
        String sql = "UPDATE users SET username = ?, nickname = ?, department = ?, grade = ? WHERE studentId = ?";
        template.update(sql, editProfileDTO.getUsername(), editProfileDTO.getNickname(), editProfileDTO.getDepartment(), editProfileDTO.getGrade(), studentId);
    }

    @Override
    public void editProfileImage(String studentId, String profileImage) {
        String sql = "update users set profileImage = ? where studentId = ?";
        template.update(sql, profileImage, studentId);
    }

    private RowMapper<User> studentRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setStudentId(rs.getString("studentId"));
            user.setPassword(rs.getString("password"));
            user.setUsername(rs.getString("username"));
            user.setNickname(rs.getString("nickname"));
            user.setDepartment(rs.getString("department"));
            user.setGrade(rs.getInt("grade"));
            user.setIsPublic(rs.getBoolean("isPublic"));
            user.setRole(rs.getString("role"));
            user.setProfileImage(rs.getString("profileImage"));
            return user;
        };
    }
}
