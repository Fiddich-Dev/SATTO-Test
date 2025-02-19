package insung.satto.login.service;



import insung.satto.login.dto.CustomUserDetails;
import insung.satto.login.entity.Student;
import insung.satto.login.repository.StudentRepository;
import insung.satto.login.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        if (student != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(student);
        }
        return null;
    }
}
