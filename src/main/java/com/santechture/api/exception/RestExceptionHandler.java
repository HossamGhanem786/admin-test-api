package com.santechture.api.exception;

import com.santechture.api.dto.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;


    @ExceptionHandler(BusinessExceptions.class)
    protected ResponseEntity<Object> handelBusinessException(BusinessExceptions ex) {

        return new ResponseEntity<Object>(GeneralResponse.builder().message(messageSource.getMessage(ex.message, null, getLocale())).code(ex.httpStatus.value()).build(), ex.httpStatus);
    }


}
