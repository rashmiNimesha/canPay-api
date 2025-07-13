package com.canpay.api.util;

import java.security.SecureRandom;
import java.util.UUID;

public class WalletNumberGenerator {

    private static final String PREFIX = "P";
    private static final String BUS_PREFIX = "B";
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateWalletNumber(UUID userId) {
        String userIdPrefix = userId.toString().substring(0, 6).toUpperCase().replace("-", "");
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            randomPart.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return String.format("%s-%s-%s", PREFIX, userIdPrefix, randomPart).substring(0, 16);
    }

    public static String generateBusWalletNumber(UUID busId) {
        String busIdPrefix = busId.toString().substring(0, 6).toUpperCase().replace("-", "");
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            randomPart.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return String.format("%s-%s-%s", BUS_PREFIX, busIdPrefix, randomPart).substring(0, 16);
    }
}
