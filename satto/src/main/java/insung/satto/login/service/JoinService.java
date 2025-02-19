package insung.satto.login.service;


import insung.satto.login.dto.JoinDTO;
import insung.satto.login.entity.Student;
import insung.satto.login.repository.StudentRepository;
import insung.satto.login.repository.StudentRepositoryJDBC;
import insung.satto.login.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        String studentId = joinDTO.getStudentId();
        String password = joinDTO.getPassword();

        Student data = new Student();

//        Student findStudent = studentRepository.findByStudentId(studentId);
        // 학번이 안겹치는지 확인하는 로직 필요

        if(studentRepository.existsByStudentId(studentId)) {
            log.info("이미 존재하는 학번입니다.");
            throw new DuplicateKeyException("이미 존재하는 학번입니다.");
        }

        data.setStudentId(studentId);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setUsername("기본이름");
        data.setNickname("기본닉네임");
        data.setDepartment("기본학과");
        data.setGrade(0);
        data.setIsPublic(true);
        log.info("joinProcess() 실행");

        studentRepository.save(data);
    }
}
