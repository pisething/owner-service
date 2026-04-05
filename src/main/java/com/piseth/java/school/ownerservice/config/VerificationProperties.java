package com.piseth.java.school.ownerservice.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.verification")
public class VerificationProperties {

    private int otpLength = 6;
    private long otpTtlSeconds = 300;
    private int maxAttempts = 5;
    private long resendCooldownSeconds = 60;
    private String otpPepper = "CHANGE-ME-IN-PROD";
}