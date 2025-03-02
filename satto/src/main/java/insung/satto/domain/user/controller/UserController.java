package insung.satto.domain.user.controller;

import insung.satto.domain.user.dto.ApiResponse;
import insung.satto.domain.user.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@ResponseBody
public class UserController {

    StudentRepository studentRepository;

    public UserController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/checkDuplicatedStudentId")
    public ApiResponse<?> checkDuplicatedStudentId(@RequestBody String studentId) {
        log.info("checkDuplicatedStudentId()");
        if(studentRepository.existsByStudentId(studentId) == true) {
            return ApiResponse.onFailure("403", "학번이 이미 존재합니다");
        }
        else {
            return ApiResponse.onSuccess(null);
        }
    }

}
