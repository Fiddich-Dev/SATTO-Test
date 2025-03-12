package insung.satto.exception;

import insung.satto.domain.user.dto.ApiResponse;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<String>> handleAllException(Exception e) {
        log.error(">>>>> Internal Server Error : ", e);
        BaseErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<String> errorResponse = ApiResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage()
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<?> duplicateKeyException(DuplicateKeyException e) {
        log.error(">>>>> DuplicateKeyException Error : ", e);
        return ApiResponse.onFailure("500", "DuplicateKeyException 오류");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> illegalArgumentException(IllegalArgumentException e) {
        log.error(">>>>> IllegalArgumentException Error : ", e);
        return ApiResponse.onFailure("500", "IllegalArgumentException 오류");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<?> noSuchElementException(NoSuchElementException e) {
        log.error(">>>>> NoSuchElementException Error : ", e);
        return ApiResponse.onFailure("500", "NoSuchElementException 오류");
    }

}
