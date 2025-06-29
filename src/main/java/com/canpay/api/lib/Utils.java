package com.canpay.api.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

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

    /**
     * Saves an image from a data URL to the system storage and returns the file
     * path.
     */
    public String saveImage(String dataUrl, String fileName) throws IOException {
        if (dataUrl == null || dataUrl.isBlank()) {
            return null;
        }

        // Extract base64 data from the data URL
        String base64Data = dataUrl.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // Ensure the storage directory exists
        File storageDir = new File(IMAGE_STORAGE_PATH);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Save the image to the storage directory
        File imageFile = new File(storageDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
        }

        return imageFile.getAbsolutePath();
    }

    /**
     * Deletes an image from the system storage.
     */
    public void deleteImage(String filePath) {
        if (filePath != null && !filePath.isBlank()) {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }

    /**
     * Converts an image file path to a data URL format.
     */
    public String convertImageToDataUrl(String filePath) {
        try {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
                String base64Data = Base64.getEncoder().encodeToString(imageBytes);
                return "data:image/png;base64," + base64Data;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to data URL", e);
        }
        return null;
    }
}
