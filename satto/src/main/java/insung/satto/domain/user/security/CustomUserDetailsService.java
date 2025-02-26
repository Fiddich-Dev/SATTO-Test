package insung.satto.domain.user.security;



import insung.satto.domain.user.entity.Student;
import insung.satto.domain.user.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public CustomUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {

        //DB에서 조회
        Student student = studentRepository.findByStudentId(studentId);
        log.info("student role = {}", student.getRole());
        if (student != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(student);
        }
        return null;
    }
}
