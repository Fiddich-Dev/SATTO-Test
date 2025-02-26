package insung.satto.domain.user.service;


import insung.satto.domain.user.dto.JoinDTO;
import insung.satto.domain.user.entity.Student;
import insung.satto.domain.user.repository.StudentRepository;
import insung.satto.domain.user.repository.StudentRepositoryJDBC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class JoinService {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public JoinService(StudentRepository studentRepository, BCryptPasswordEncoder bCryptPasswordEncoder, StudentRepositoryJDBC studentRepositoryJDBC) {
        this.studentRepository = studentRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Transactional
    public void joinProcess(JoinDTO joinDTO) throws DuplicateKeyException{
        log.info("joinProcess() 실행");

        String studentId = joinDTO.getStudentId();
        String password = joinDTO.getPassword();
        String username = joinDTO.getUsername();
        String nickname = joinDTO.getNickname();
        String department = joinDTO.getDepartment();
        Integer grade = joinDTO.getGrade();
        Boolean isPublic = joinDTO.getIsPublic();


        // 학번이 안겹치는지 확인하는 로직
        if(studentRepository.existsByStudentId(studentId)) {
            log.info("이미 존재하는 학번입니다.");
            throw new DuplicateKeyException("이미 존재하는 학번입니다.");
        }

        Student data = new Student();

        data.setStudentId(studentId);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setUsername(username);
        data.setNickname(nickname);
        data.setDepartment(department);
        data.setGrade(grade);
        data.setIsPublic(isPublic);
        data.setRole("ADMIN");

        studentRepository.save(data);
    }
}
