package com.example.employee.exception;

import com.example.employee.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: ", ex);
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Result.error(message);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        Result<String> result = new Result<>();
        result.setCode(ex.getCode());
        result.setMessage(ex.getMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleGenericException(Exception ex) {
        log.error("System error: ", ex);
        return Result.error("System Error: " + ex.getMessage());
    }
}
