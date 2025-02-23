package insung.satto.login.repository;

import insung.satto.login.entity.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
        System.out.println("count = " + count);
        return count >= 1;
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
