package com.ll.coffeeBean.global.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.coffeeBean.global.appConfig.AppConfig;
import com.ll.coffeeBean.global.exceptions.ServiceException;
import com.ll.coffeeBean.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseAspect {
    private final HttpServletResponse response;

    @Around("""
            (
                within
                (
                    @org.springframework.web.bind.annotation.RestController *
                )
                &&
                (
                    @annotation(org.springframework.web.bind.annotation.GetMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PostMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PutMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.DeleteMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.RequestMapping)
                )
            )
            ||
            @annotation(org.springframework.web.bind.annotation.ResponseBody)
            """)
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        // 현재 실행 중인 클래스 패키지 이름
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();

        // 동적으로 로거 생성
        Logger log = LoggerFactory.getLogger(packageName);

        try {
            Object proceed = joinPoint.proceed();

            if (proceed instanceof RsData<?>) {
                RsData<?> rsData = (RsData<?>) proceed;
                response.setStatus(rsData.getStatusCode());

                ObjectMapper objectMapper = AppConfig.getObjectMapper();
                String jsonData = objectMapper.writeValueAsString(rsData.getData());
                log.info("[INFO] Code : {}, Message : {}, Data : {}", rsData.getStatusCode(), rsData.getMsg(),
                        jsonData);
            }

            return proceed;
        } catch (ServiceException ex) {
            log.error("[ERROR] Code : {}, Message : {} ", ex.getResultCode(), ex.getMsg());
            throw ex;
        } catch (ResponseStatusException ex) {
            log.error("[ERROR] Code : {}, Message : {} ", ex.getStatusCode().value(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("[ERROR] Code : [000], Message : {} ", ex.getMessage());
            throw ex;
        }
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object handleScheduling(ProceedingJoinPoint joinPoint) throws Throwable {
        String packageName = joinPoint.getTarget().getClass().toString().replace("class ", "");
        Logger log = LoggerFactory.getLogger(packageName);

        log.info("Start Scheduled!!");

        Object result = joinPoint.proceed();

        log.info("End Scheduled!!");

        return result;
    }
}