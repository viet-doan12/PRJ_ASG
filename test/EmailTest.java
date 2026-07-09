/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.flowershop.util.OTPUtil;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
/**
 *
 * @author ADMIN
 */
public class EmailTest {
    public static void main(String[] args) {

        String otp = OTPUtil.generateOTP();

        System.out.println("OTP = " + otp);

        long expire = OTPUtil.getExpireTime();

        System.out.println(expire);

        System.out.println(OTPUtil.isExpired(expire));

    }
}
