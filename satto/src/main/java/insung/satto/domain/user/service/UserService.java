package insung.satto.domain.user.service;



import insung.satto.domain.user.dto.EditProfileDTO;
import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.QuerydslUserRepository;
import insung.satto.domain.user.repository.SpringDataJpaUserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    @Value("${file.dir}")
    private String fileDir;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=";

    private final QuerydslUserRepository querydslUserRepository;
    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;


    public User findById(Long id) {
        return springDataJpaUserRepository.findById(id).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + id));
    }

    // true면 학번이 db에 있음
    public boolean existsByStudentId(String studentId) {
        log.info("existsByStudentId()");
        return querydslUserRepository.existsByStudentId(studentId);
    }

    public void changePublicStatus(Long id) {
        log.info("changePublicStatus");
        User findUser = findById(id);
        findUser.setIsPublic(!findUser.getIsPublic());
    }

    public void withdrawal(Long id) {
        findById(id);
        // redis에서 studentId + ":refreshToken" 키 삭제
        redisTemplate.delete(id + ":refreshToken");
        springDataJpaUserRepository.deleteById(id);
    }

    public boolean verifyCurrentPassword(Long id, String inputPassword) {
        User findUser = findById(id);
        return bCryptPasswordEncoder.matches(inputPassword, findUser.getPassword());
    }

    public void changePassword(Long id, String inputPassword) {
        User findUser = findById(id);
        String newPassword = bCryptPasswordEncoder.encode(inputPassword);
        findUser.setPassword(inputPassword);
    }

    public void editProfile(Long id, EditProfileDTO editProfileDTO) {
        User findUser = findById(id);
        findUser.setUsername(editProfileDTO.getUsername());
        findUser.setNickname(editProfileDTO.getNickname());
        findUser.setDepartment(editProfileDTO.getDepartment());
        findUser.setGrade(editProfileDTO.getGrade());
    }

    public void sendNewPassword(Long id) {
        User findUser = findById(id);
        // 랜덤 비밀번호 생성
        Random random = new SecureRandom();
        String password = "";
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password += CHARACTERS.charAt(index);
        }
        // 비밀번호 암호화
        String Encodedpassword = bCryptPasswordEncoder.encode(password);
        // 암호화된 비밀번호로 db변경
        findUser.setPassword(Encodedpassword);
        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String email = id + "@sangmyung.kr";
            helper.setTo(email);
            helper.setSubject("임시 비밀번호");
            helper.setText("<h3>임시 비밀번호: <strong>" + password + "</strong></h3>", true); // HTML 형식

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    public String uploadProfileImage(Long id, MultipartFile file) throws IOException {
        User findUser = findById(id);
        // 1. 파일 확장자 확인
        String originalFilename = file.getOriginalFilename();
        String extension = Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFilename.lastIndexOf(".")))
                .orElse("");

        if (!isValidImageExtension(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        // 2. 저장할 파일명 생성 (고유한 이름)
        String newFileName = UUID.randomUUID().toString() + extension;
        log.info("newFileName = {}", newFileName);

        // 3. 저장할 파일 경로 설정
        Path filePath = Paths.get(fileDir, newFileName);
        log.info("filePath = {}", filePath);

        // 4. 디렉토리 생성 (존재하지 않으면 생성)
        // 모르겠음
        Files.createDirectories(filePath.getParent());

        // 5. 파일 저장
        file.transferTo(filePath.toFile());

        // db에 프로필 이미지 경로 수정
        findUser.setProfileImage(fileDir + "/" + newFileName);

        // 6. 업로드된 파일 URL 반환
        return fileDir + "/" + newFileName;
    }

    public void deleteProfileImage(Long id) {
        User findUser = findById(id);
        findUser.setProfileImage(null);
    }

    // 이미지 파일 확장자 검사
    private boolean isValidImageExtension(String extension) {
        return extension.equalsIgnoreCase(".jpg") ||
                extension.equalsIgnoreCase(".jpeg") ||
                extension.equalsIgnoreCase(".png") ||
                extension.equalsIgnoreCase(".gif");
    }
}

