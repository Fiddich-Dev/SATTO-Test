package insung.satto.domain.user.repository;

import insung.satto.domain.user.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class StudentRepositoryJDBC implements StudentRepository{

    private final JdbcTemplate template;

    public StudentRepositoryJDBC(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Student save(Student student) {
        String sql = "insert into student(studentId, password, username, nickname, department, grade, isPublic, role) values (?, ?, ?, ?, ?, ?, ?, ?)";
        template.update(sql, student.getStudentId(), student.getPassword(), student.getUsername(), student.getNickname(), student.getDepartment(), student.getGrade(), student.getIsPublic(), student.getRole());
        return student;
    }

    @Override
    public Student findByStudentId(String studentId) {
        String sql = "select * from student where studentId = ?";
        Student findStudent = template.queryForObject(sql, studentRowMapper(), studentId);
        return findStudent;
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        String sql = "select count(*) from student where studentId = ?";
        Integer count = template.queryForObject(sql, Integer.class, studentId);
        log.info("count = {}", count);
        return count >= 1;
    }

    @Override
    public void changePublicStatus(boolean nowStatus, String studentId) {
        String sql = "update student set isPublic = ? where studentId = ?";
        template.update(sql, !nowStatus, studentId);
    }

    @Override
    public void withdrawal(String studentId) {
        String sql = "delete from student where studentId = ?";
        template.update(sql, studentId);
    }

    @Override
    public void changePassword(String password, String studentId) {
        String sql = "update student set password = ? where studentId = ?";
        template.update(sql, password, studentId);
    }

    @Override
    public void editProfile(String studentId, String username, String nickname, String department, Integer grade) {
        String sql = "UPDATE student SET username = ?, nickname = ?, department = ?, grade = ? WHERE studentId = ?";
        template.update(sql, username, nickname, department, grade, studentId);
    }

    private RowMapper<Student> studentRowMapper() {
        return (rs, rowNum) -> {
            Student student = new Student();
            student.setStudentId(rs.getString("studentId"));
            student.setPassword(rs.getString("password"));
            student.setUsername(rs.getString("username"));
            student.setNickname(rs.getString("nickname"));
            student.setDepartment(rs.getString("department"));
            student.setGrade(rs.getInt("grade"));
            student.setIsPublic(rs.getBoolean("isPublic"));
            student.setRole(rs.getString("role"));
            return student;
        };
    }
}
