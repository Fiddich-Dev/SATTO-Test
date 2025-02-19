package insung.satto.login.controller;

import insung.satto.login.dto.ApiResponse;
import insung.satto.login.dto.JoinDTO;
import insung.satto.login.entity.Student;
import insung.satto.login.service.JoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ApiResponse<?> joinProcess(@RequestBody JoinDTO joinDTO) {
        try {
            joinService.joinProcess(joinDTO);
            ApiResponse<?> response = ApiResponse.onSuccess(null);
            return response;
        } catch (DuplicateKeyException e) {
            log.info("DuplicateKeyException", e);
            ApiResponse<?> response = ApiResponse.onFailure("403", "이미 가입된 학번입니다.");
            return response;
        }
    }


}
