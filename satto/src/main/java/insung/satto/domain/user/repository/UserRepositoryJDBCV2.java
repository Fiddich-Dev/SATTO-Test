package insung.satto.domain.user.repository;

import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class UserRepositoryJDBCV2 implements UserRepository {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public UserRepositoryJDBCV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public User save(User user) {
//        String sql = "insert into users(studentId, password, username, nickname, department, grade, isPublic, role, profileImage) values (:studentId, :password, :username, :nickname, :department, :grade, :isPublic, :role, :profileImage)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(user);
        Number key = jdbcInsert.executeAndReturnKey(param);
        user.setId(key.intValue());
        return user;
//        template.update(sql, param);
//        return user;
    }

    @Override
    public User findByStudentId(String studentId) {
        String sql = "select * from users where studentId = :studentId";
        Map<String, Object> param = Map.of("studentId", studentId);
        User findUser = template.queryForObject(sql, param, studentRowMapper());
        return findUser;
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        String sql = "select count(*) from users where studentId = :studentId";
        Map<String, Object> param = Map.of("studentId", studentId);
        Integer count = template.queryForObject(sql, param, Integer.class);
        log.info("count = {}", count);
        return count == 1;
    }

    @Override
    public void toggleAccountPrivacy(boolean currentStatus, String studentId) {
        String sql = "update users set isPublic = :currentStatus where studentId = :studentId";
        Map<String, Object> param = Map.of("currentStatus", !currentStatus, "studentId", studentId);
        template.update(sql, param);
    }

    @Override
    public void withdrawal(String studentId) {
        String sql = "delete from users where studentId = :studentId";
        Map<String, Object> param = Map.of("studentId", studentId);
        template.update(sql, param);
    }

    @Override
    public void changePassword(String password, String studentId) {
        String sql = "update users set password = :password where studentId = :studentId";
        Map<String, Object> param = Map.of("password", password, "studentId", studentId);
        template.update(sql, param);
    }

    @Override
    public void editProfile(String studentId, EditProfileDTO editProfileDTO) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("studentId", studentId)
                .addValue("username", editProfileDTO.getUsername())
                .addValue("nickname", editProfileDTO.getNickname())
                .addValue("department", editProfileDTO.getDepartment())
                .addValue("grade", editProfileDTO.getGrade());

        String sql = "UPDATE users SET username = :username, nickname = :nickname, department = :department, grade = :grade WHERE studentId = :studentId";
        template.update(sql, param);
    }

    @Override
    public void editProfileImage(String studentId, String profileImage) {
        Map<String, Object> param = Map.of("studentId", studentId, "profileImage", profileImage);
        String sql = "update users set profileImage = :profileImage where studentId = :studentId";
        template.update(sql, param);
    }

    private RowMapper<User> studentRowMapper() {
            return BeanPropertyRowMapper.newInstance(User.class);
    }
}
