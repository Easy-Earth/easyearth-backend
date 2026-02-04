//스웨거 버전이 낮아서 오류 메시지 출력 기능 사용 불가
// package com.kh.spring.common.exception;

// import java.util.NoSuchElementException;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @RestControllerAdvice //RestController에서 발생하는 예외를 처리하도록 설정
// public class GlobalExceptionHandler {

//     // 1. 잘못된 요청 (IllegalArgumentException) -> 400 Bad Request
//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
//         log.warn("IllegalArgumentException: {}", ex.getMessage());
//         return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
//     }

//     // 2. 리소스 없음 (NoSuchElementException) -> 404 Not Found
//     @ExceptionHandler(NoSuchElementException.class)
//     public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
//         log.warn("NoSuchElementException: {}", ex.getMessage());
//         return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
//     }

//     // 3. 그 외 서버 에러 (Exception) -> 500 Internal Server Error
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
//         log.error("Internal Server Error: ", ex);
//         return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", request);
//     }

//     // 에러 응답 생성 헬퍼 메서드
//     private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
//         return ResponseEntity.status(status)
//                 .body(ErrorResponse.of(
//                         status.value(),
//                         status.getReasonPhrase(),
//                         message,
//                         request.getRequestURI()
//                 ));
//     }
// }
