package com.canpay.api.service.implementation;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final EmailService emailService;

    public OTPService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
        System.out.println("OTP for " + email + " is: " + otp);
    }

    public boolean verifyOtp(String email, String otp) {
        System.out.println("OTP verified in service");
        return otp.equals(otpStorage.get(email));

    }
}
