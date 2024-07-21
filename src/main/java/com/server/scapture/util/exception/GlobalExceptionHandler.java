package com.server.scapture.util.exception;

import com.server.scapture.util.response.CustomAPIResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice // 이 클래스가 전역적으로 예외 처리를 할 것임을 명시
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException 예외가 발생하였을 때 수행할 동작을 작성한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 반환타입은 기존의 응답과 동일하게 처리하기 위해 ResponseEntity<CustomAPIResponse>로 작성한다.

        // 발생핞 에러로부터 에러메시지를 얻어온다.
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        // System.out.println("errorMessage = " + errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomAPIResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // 데이터베이스 작업 중 제약조건을 위반하는 경우 발생하는 예외
    // Primary Key 위반, Unique 제약 조건 위반, Foregin Key 위반, Not Null 위반,등
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomAPIResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }
}


