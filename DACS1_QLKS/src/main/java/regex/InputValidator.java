package regex;
import java.util.regex.Pattern;
public class InputValidator {
//Regex pattern kiểm tra email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    // Regex pattern kiểm tra số điện thoại (Việt Nam)
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(0|\\+84)[1-9][0-9]{8}$");
    // Kiểm tra email hợp lệ
    public  boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Kiểm tra số điện thoại hợp lệ (Việt Nam)
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
