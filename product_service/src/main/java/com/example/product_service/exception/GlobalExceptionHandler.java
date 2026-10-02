package com.example.product_service.exception;


import com.example.product_service.common.BaseResponse;
import com.example.product_service.common.FieldViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldViolation> violations = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            violations.add(new FieldViolation(fieldName, errorMessage));
        }

        BaseResponse<Object> response = new BaseResponse<>();
        response.getMeta().setCode(HttpStatus.BAD_REQUEST.value());
        response.getMeta().setErrors(violations);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.getMeta().setCode(HttpStatus.BAD_REQUEST.value());
        response.getMeta().setMessage("Path variable validation failed");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.getMeta().setCode(HttpStatus.BAD_REQUEST.value());
        response.getMeta().setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(AuthorizationDeniedException.class)
//    public ResponseEntity<BaseResponse<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
//        BaseResponse<Object> response = new BaseResponse<>();
//        response.getMeta().setCode(HttpStatus.FORBIDDEN.value());
//        response.getMeta().setMessage("Access Denied,");
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.getMeta().setCode(HttpStatus.NOT_FOUND.value());
        response.getMeta().setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
