package com.flowershop.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    // =========================
    // Regex
    // =========================
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Việt Nam: 10 số, bắt đầu bằng 0
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0\\d{9}$");

    // Ít nhất 6 ký tự, có chữ và số
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");

    // =========================
    // Empty
    // =========================
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // =========================
    // Full Name
    // =========================
    public static boolean isValidFullName(String fullName) {

        if (isEmpty(fullName)) {
            return false;
        }

        fullName = fullName.trim();

        return fullName.length() >= 2
                && fullName.length() <= 100;
    }

    // =========================
    // Email
    // =========================
    public static boolean isValidEmail(String email) {

        if (isEmpty(email)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // =========================
    // Phone
    // =========================
    public static boolean isValidPhone(String phone) {

        if (isEmpty(phone)) {
            return false;
        }

        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // =========================
    // Password
    // =========================
    public static boolean isValidPassword(String password) {

        if (isEmpty(password)) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // =========================
    // SHA-256 Hash
    // =========================
    public static String hashPassword(String password) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {

                sb.append(String.format("%02x", b));

            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);

        }

    }

    // =========================
    // Login Check
    // =========================
    public static boolean matchesPassword(String rawPassword,
                                          String hashedPassword) {

        if (rawPassword == null || hashedPassword == null) {
            return false;
        }

        return hashPassword(rawPassword).equals(hashedPassword);
    }

}