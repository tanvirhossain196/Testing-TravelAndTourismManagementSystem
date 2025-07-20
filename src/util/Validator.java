package util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+88)?01[3-9]\\d{8}$");

    public static boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validatePhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isValidRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && 
               Pattern.matches("^[a-zA-Z\\s]+$", name.trim());
    }
}
