package com.asl.prd004.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DefinitionException.class)
    public ResultGenerator misDefinitionExceptionHandler(HttpServletResponse response, DefinitionException e) {
        response.setStatus(e.getErrorCode());
        return new ResultGenerator<>(e.getErrorCode(),e.getErrorMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResultGenerator<String> exceptionHandler(Exception e) {
        log.error("exceptionHandler...",e);
        return ResultGenerator.getFailResult(e.getMessage());
    }
}
