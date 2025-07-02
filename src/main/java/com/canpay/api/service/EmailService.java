package com.canpay.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending emails, specifically OTP verification emails.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${fromEmail}")
    private String fromEmail;

    @Value("${fromName}")
    private String fromName;

    /**
     * Sends an OTP verification email to the specified recipient.
     *
     * @param toEmail the recipient's email address
     * @param otp     the one-time password to include in the email
     * @throws RuntimeException if sending fails
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // Set the "From" address with display name
            helper.setFrom(fromEmail, fromName);

            // Set the recipient
            helper.setTo(toEmail);

            // Set the subject
            helper.setSubject(otp + " is your verification code");

            // Generate current timestamp
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Load and populate HTML template
            String htmlContent = loadTemplate("otp-email-template.html")
                    .replace("${otp}", otp)
                    .replace("${timestamp}", timestamp);

            // Set the email content as HTML
            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(mimeMessage);

            logger.info("OTP email sent to {} from {} ({})", toEmail, fromEmail, fromName);

        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Loads an email template from the classpath.
     *
     * @param path the path to the template file
     * @return the template content as a String
     * @throws RuntimeException if the template cannot be loaded
     */
    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template: " + path, e);
        }
    }
}