package contactmanagementsystem;

public class Validator {
 
    public static String checkName(String name) {
        if (name == null || name.trim().length() == 0) {
            return "Name cannot be empty.";
        }
        if (name.length() > 50) {
            return "Name is too long (max 50 characters).";
        }
        boolean hasLetter = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '|' || c < 32) {
                return "Name cannot contain the '|' symbol or control characters.";
            }
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
        }
        if (!hasLetter) {
            return "Name must contain at least one letter.";
        }
        return null;
    }
 
    public static String checkPhone(String phone, TelecoProvider provider) {
        if (phone == null || phone.length() == 0) {
            return "Phone number cannot be empty.";
        }
        for (int i = 0; i < phone.length(); i++) {
            if (!isDigit(phone.charAt(i))) {
                return "Phone number must contain digits only (no spaces, dashes or +63).";
            }
        }
        if (phone.length() != 11) {
            return "Phone number must be exactly 11 digits (example: 09171234567).";
        }
 
        if (!provider.isValidPrefix(phone)) {
            return "Invalid prefix for " + provider.getName() + ".\n"
                 + "    Allowed prefixes: " + provider.getAllPrefixes();
        }
        return null;
    }
 
    public static String checkEmailOptional(String email) {
        if (email == null || email.length() == 0) {
            return null;
        }
        return checkEmail(email);
    }
 
    public static String checkEmail(String email) {
        if (email == null || email.length() == 0) {
            return "Email cannot be empty.";
        }
        if (email.length() > 100) {
            return "Email is too long (max 100 characters).";
        }
 
        int at = -1;
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if (c == ' ') {
                return "Email cannot contain spaces.";
            }
            if (c == '@') {
                if (at != -1) {
                    return "Email can only have one '@'.";
                }
                at = i;
            }
        }
        if (at == -1) {
            return "Email must contain '@' (example: name@gmail.com).";
        }
 
        String local  = email.substring(0, at);
        String domain = email.substring(at + 1);
 
        if (local.length() == 0) {
            return "Email needs some text before the '@'.";
        }
        if (domain.length() == 0) {
            return "Email needs a domain after the '@' (example: gmail.com).";
        }
 
        for (int i = 0; i < local.length(); i++) {
            char c = local.charAt(i);
            if (!isLetter(c) && !isDigit(c) && c != '.' && c != '_' && c != '%' && c != '+' && c != '-') {
                return "Email has an invalid character before the '@': " + c;
            }
        }
        if (local.charAt(0) == '.' || local.charAt(local.length() - 1) == '.' || local.indexOf("..") != -1) {
            return "Email cannot start or end with '.' or have '..' before the '@'.";
        }
 
        for (int i = 0; i < domain.length(); i++) {
            char c = domain.charAt(i);
            if (!isLetter(c) && !isDigit(c) && c != '.' && c != '-') {
                return "Email has an invalid character in the domain: " + c;
            }
        }
        int lastDot = domain.lastIndexOf('.');
        if (lastDot == -1) {
            return "Email domain needs a '.' (example: gmail.com).";
        }
        if (domain.charAt(0) == '.' || domain.charAt(0) == '-'
                || domain.charAt(domain.length() - 1) == '.' || domain.charAt(domain.length() - 1) == '-'
                || domain.indexOf("..") != -1 || domain.indexOf(".-") != -1 || domain.indexOf("-.") != -1) {
            return "Email domain is not written correctly (check the '.' and '-').";
        }
        String ending = domain.substring(lastDot + 1);
        if (ending.length() < 2) {
            return "Email must end with at least 2 letters after the last '.' (like .com or .ph).";
        }
        for (int i = 0; i < ending.length(); i++) {
            if (!isLetter(ending.charAt(i))) {
                return "Email must end with letters after the last '.' (like .com or .ph).";
            }
        }
        return null;
    }
 
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
 
    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
 