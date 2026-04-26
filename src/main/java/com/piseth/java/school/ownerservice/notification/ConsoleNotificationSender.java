package com.piseth.java.school.ownerservice.notification;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.domain.enums.VerificationType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

//@Primary
@Component
@Slf4j
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public Mono<Void> send(String target, VerificationType type, String otp) {
        //log.info("Console OTP sent. type={}, target={}, otp={}", type, target, otp);
        //return Mono.empty();
        
        return Mono.fromRunnable(() -> {
            log.info("Console OTP sent. type={}, target={}, otp={}", type, target, otp);
        });
    }
}