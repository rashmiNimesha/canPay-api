package com.canpay.api.service.implementation;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void sendOtp(String phone) {
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        otpStorage.put(phone, otp);
        System.out.println("OTP for " + phone + " is: " + otp); // mock SMS
    }

    public boolean verifyOtp(String phone, String otp) {
        return otp.equals(otpStorage.get(phone));
    }
}
