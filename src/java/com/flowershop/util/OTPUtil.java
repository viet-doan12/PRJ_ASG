package com.flowershop.util;

import java.security.SecureRandom;

public class OTPUtil {

    // Độ dài OTP
    private static final int OTP_LENGTH = 6;

    // OTP hết hạn sau 5 phút
    public static final long OTP_EXPIRE_TIME = 5 * 60 * 1000;

    // SecureRandom an toàn hơn Random
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Sinh OTP ngẫu nhiên gồm 6 chữ số
     */
    public static String generateOTP() {

        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10));
        }

        return otp.toString();
    }

    /**
     * Trả về thời điểm OTP sẽ hết hạn
     */
    public static long getExpireTime() {
        return System.currentTimeMillis() + OTP_EXPIRE_TIME;
    }

    /**
     * Kiểm tra OTP còn hạn không
     */
    public static boolean isExpired(long expireTime) {
        return System.currentTimeMillis() > expireTime;
    }

}