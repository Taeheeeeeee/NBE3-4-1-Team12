package com.ll.coffeeBean.global.globalExceptionHandler;

import com.ll.coffeeBean.global.appConfig.AppConfig;
import com.ll.coffeeBean.global.exceptions.ServiceException;
import com.ll.coffeeBean.global.rsData.RsData;
import com.ll.coffeeBean.standard.base.Empty;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RsData<Empty>> handle(NoHandlerFoundException ex) {

        if (AppConfig.isNotProd()) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Empty>> handle(NoSuchElementException ex) {

        if (AppConfig.isNotProd()) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Empty>> handle(MethodArgumentNotValidException ex) {
        if (AppConfig.isNotProd()) {
            ex.printStackTrace();
        }

        String packageName = ex.getParameter().getGenericParameterType().toString().replace("class ", "");
        Logger log = LoggerFactory.getLogger(packageName);

        String message = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                .sorted(Comparator.comparing(String::toString))
                .collect(Collectors.joining("\n"));

        log.error(message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-1",
                        message
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Empty>> handle(ServiceException ex) {

        if (AppConfig.isNotProd()) {
            ex.printStackTrace();
        }

        RsData<Empty> rsData = ex.getRsData();

        return ResponseEntity
                .status(rsData.getStatusCode())
                .body(rsData);
    }
}