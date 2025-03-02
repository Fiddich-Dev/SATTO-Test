package insung.satto.domain.user.service;


import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.dto.ChangePasswordDto;
import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.Student;
import insung.satto.domain.user.repository.StudentRepository;
import insung.satto.domain.user.security.jwt.JWTUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.SecureRandom;
import java.util.Random;


@Slf4j
@Service
public class UserService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=";

    private final StudentRepository studentRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;


    public UserService(StudentRepository studentRepository, JWTUtil jwtUtil, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender) {
        this.studentRepository = studentRepository;
        this.jwtUtil = jwtUtil;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
    }

    public boolean checkDuplicatedStudentId(String studentId) {
        log.info(studentRepository.getClass().toString());
        log.info("입력된 studentId: {}", studentId);
        boolean exists = studentRepository.existsByStudentId(studentId);
        log.info("DB 조회 결과: {}", exists);
        return exists;
    }

    public void changePublicStatus(boolean nowStatus, String studentId) {
        log.info("changePublicStatus");
        // 학생 존재 여부 확인
        if(studentRepository.existsByStudentId(studentId)) {
            studentRepository.changePublicStatus(nowStatus, studentId);
        }
        else {
            throw new DuplicateKeyException("없는 학번입니다");
        }
    }

    public Student getMyAccountInfoProcess(String accessToken) {
        String studentId = jwtUtil.getStudentId(accessToken);
        return studentRepository.findByStudentId(studentId);
    }

    public void withdrawal(String studentId) {
        studentRepository.withdrawal(studentId);
        // 리프레쉬 토큰 db삭제해야할까??
    }

    public void changePassword(Student student, ChangePasswordDto changePasswordDto) {
        // 현재비밀번호가 맞는지확인
        if(bCryptPasswordEncoder.matches(changePasswordDto.getCurrentPassword(), student.getPassword()) == false) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 학번으로 바꾸고 싶은 비밀번호로 변경
        String newPassword = bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword());
        studentRepository.changePassword(newPassword, student.getStudentId());
    }

    public void sendNewPassword(String studentId) {
        Random random = new SecureRandom();
        String password = "";

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password += CHARACTERS.charAt(index);
        }
//        password = bCryptPasswordEncoder.encode(password);
        studentRepository.changePassword(bCryptPasswordEncoder.encode(password), studentId);
        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(studentId + "@sangmyung.kr");
            helper.setSubject("임시 비밀번호");
            helper.setText("<h3>임시 비밀번호: <strong>" + password + "</strong></h3>", true); // HTML 형식

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    public void editProfile(String studentId, EditProfileDTO editProfileDTO) {
        String name = editProfileDTO.getUsername();
        String nickname = editProfileDTO.getNickname();
        String department = editProfileDTO.getDepartment();
        Integer grade = editProfileDTO.getGrade();

        studentRepository.editProfile(studentId, name, nickname, department, grade);
    }
}

