package com.canpay.api.lib;

import java.security.SecureRandom;

import com.canpay.api.repository.dashboard.DPassengerWalletRepository;

import lombok.experimental.UtilityClass;

import static com.canpay.api.lib.Constants.*;

@UtilityClass
public class Utils {
    /**
     * Generates a unique 16-digit wallet number.
     */
    public String generateUniqueWalletNumber(DPassengerWalletRepository passengerWalletRepository) {
        String walletNumber;
        SecureRandom random = new SecureRandom();
        int attempts = 0;
        do {
            if (attempts++ >= WALLET_NUMBER_MAX_ATTEMPTS) {
                throw new IllegalStateException(
                        "Unable to generate unique wallet number after " + WALLET_NUMBER_MAX_ATTEMPTS + " attempts");
            }
            // Generate random 16-digit number
            walletNumber = String.format("%0" + WALLET_NUMBER_LENGTH + "d",
                    Math.abs(random.nextLong()) % WALLET_NUMBER_MODULO);
        } while (passengerWalletRepository.findByWalletNumber(walletNumber).isPresent());
        return walletNumber;
    }

}
