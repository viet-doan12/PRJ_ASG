package com.flowershop.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtil {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream is = EmailUtil.class.getClassLoader()
                .getResourceAsStream("mail.properties")) {

            if (is == null) {
                throw new RuntimeException("Không tìm thấy file mail.properties");
            }

            CONFIG.load(is);

        } catch (IOException e) {
            throw new RuntimeException("Không đọc được mail.properties", e);
        }
    }

    /**
     * Gửi Email HTML
     */
    public static void sendEmail(String to,
                                 String subject,
                                 String htmlContent)
            throws MessagingException {

        final String username = CONFIG.getProperty("mail.username");
        final String password = CONFIG.getProperty("mail.password");

        Properties props = new Properties();

        props.put("mail.smtp.host", CONFIG.getProperty("mail.host"));
        props.put("mail.smtp.port", CONFIG.getProperty("mail.port"));

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props,
                new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Bật debug SMTP
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(username));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to));

        message.setSubject(subject, "UTF-8");

        message.setContent(
                htmlContent,
                "text/html; charset=UTF-8");

        Transport.send(message);
    }

    /**
     * Gửi mã OTP
     */
    public static void sendOTP(String email,
                               String otp)
            throws MessagingException {

        String subject = "Flower Shop - Email Verification";

        String html =
                "<html>"
                + "<body style='font-family:Arial'>"
                + "<h2>Flower Shop</h2>"
                + "<p>Xin chào,</p>"
                + "<p>Mã xác nhận đăng ký của bạn là:</p>"
                + "<h1 style='color:red'>" + otp + "</h1>"
                + "<p>Mã OTP có hiệu lực trong <b>5 phút</b>.</p>"
                + "<br>"
                + "<p>Xin cảm ơn!</p>"
                + "</body>"
                + "</html>";

        sendEmail(email, subject, html);
    }

}