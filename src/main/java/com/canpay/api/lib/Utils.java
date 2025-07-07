package com.canpay.api.lib;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import com.canpay.api.repository.dashboard.DWalletRepository;

import lombok.experimental.UtilityClass;

import static com.canpay.api.lib.Constants.*;

@UtilityClass
public class Utils {
    /**
     * Generates a unique 16-digit wallet number.
     */
    public static String generateUniqueWalletNumber(DWalletRepository walletRepository) {
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
        } while (walletRepository.findByWalletNumber(walletNumber).isPresent());
        return walletNumber;
    }

    /**
     * Saves an image from a data URL to the system storage and returns the file
     * path.
     */
    public static String saveImage(String dataUrl, String fileName) throws IOException {
        if (dataUrl == null || dataUrl.isBlank()) {
            return null;
        }

        // Extract base64 data from the data URL
        String base64Data = dataUrl.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // Read image from bytes
        java.awt.image.BufferedImage image;
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(imageBytes)) {
            image = javax.imageio.ImageIO.read(bais);
        }
        if (image == null) {
            throw new IOException("Invalid image data");
        }

        // Save to src/main/resources/static/
        File storageDir = new File("src/main/resources/static/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Always save as JPG (force extension to .jpg)
        String jpgFileName = fileName.replaceAll("\\.[^.]+$", "") + ".jpg";
        File imageFile = new File(storageDir, jpgFileName);
        javax.imageio.ImageIO.write(image, "jpg", imageFile);

        return imageFile.getAbsolutePath();
    }

    /**
     * Deletes an image from the system storage.
     */
    public static void deleteImage(String filePath) {
        if (filePath != null && !filePath.isBlank()) {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }

}
