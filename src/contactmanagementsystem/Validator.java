package contactmanagementsystem;

public class Validator {
 
    public static String checkName(String name) {
        if (name == null || name.trim().length() == 0) {
            return "Please type the contact's name.";
        }
        if (name.length() > 50) {
            return "That name is too long. Please use 50 letters or fewer.";
        }
        boolean hasLetter = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '|' || c < 32) {
                return "The name cannot contain the | symbol. Please type the name again without it.";
            }
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
        }
        if (!hasLetter) {
            return "The name needs at least one letter. Please type the name again.";
        }
        return null;
    }
 
    public static String checkPhoneFormat(String phone) {
        if (phone == null || phone.length() == 0) {
            return "Please type the phone number. Example: 09*********";
        }
        for (int i = 0; i < phone.length(); i++) {
            if (!isDigit(phone.charAt(i))) {
                return "Please type numbers only, without spaces, dashes or +63. Example: 09*********";
            }
        }
        if (phone.length() != 11) {
            return "A mobile number has 11 digits, but you typed " + phone.length()
                 + ". Example: 09*********";
        }
 
        if (phone.charAt(0) != '0') {
            return "A mobile number starts with 0. Example: 09*********";
        }
        return null;
    }
 
    public static String checkPhone(String phone, TelecoProvider provider) {
        String formatError = checkPhoneFormat(phone);
        if (formatError != null) {
            return formatError;
        }
        if (!provider.isValidPrefix(phone)) {
            return "This does not look like a " + provider.getName() + " number (it starts with "
                 + phone.substring(0, 4) + ").";
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
            return "Please type the email address, for example name@gmail.com";
        }
        if (email.length() > 100) {
            return "That email address is too long. Please use 100 characters or fewer.";
        }
 
        int at = -1;
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if (c == ' ') {
                return "An email address cannot have spaces. Example: name@gmail.com";
            }
            if (c == '@') {
                if (at != -1) {
                    return "An email address can only have one @ sign. Example: name@gmail.com";
                }
                at = i;
            }
        }
        if (at == -1) {
            return "An email address needs the @ sign. Example: name@gmail.com";
        }
 
        String local  = email.substring(0, at);
        String domain = email.substring(at + 1);
 
        if (local.length() == 0) {
            return "Please type something before the @ sign. Example: name@gmail.com";
        }
        if (domain.length() == 0) {
            return "Please type the email provider after the @ sign. Example: name@gmail.com";
        }
 
        for (int i = 0; i < local.length(); i++) {
            char c = local.charAt(i);
            if (!isLetter(c) && !isDigit(c) && c != '.' && c != '_' && c != '%' && c != '+' && c != '-') {
                return "The character '" + c + "' cannot be used before the @ sign.";
            }
        }
        if (local.charAt(0) == '.' || local.charAt(local.length() - 1) == '.' || local.indexOf("..") != -1) {
            return "The part before the @ sign cannot start or end with a dot, or have two dots in a row.";
        }
 
        for (int i = 0; i < domain.length(); i++) {
            char c = domain.charAt(i);
            if (!isLetter(c) && !isDigit(c) && c != '.' && c != '-') {
                return "The character '" + c + "' cannot be used after the @ sign.";
            }
        }
        int lastDot = domain.lastIndexOf('.');
        if (lastDot == -1) {
            return "After the @ sign you need a full name with a dot, like gmail.com";
        }
        if (domain.charAt(0) == '.' || domain.charAt(0) == '-'
                || domain.charAt(domain.length() - 1) == '.' || domain.charAt(domain.length() - 1) == '-'
                || domain.indexOf("..") != -1 || domain.indexOf(".-") != -1 || domain.indexOf("-.") != -1) {
            return "The part after the @ sign is not written correctly. It should look like gmail.com";
        }
        String ending = domain.substring(lastDot + 1);
        if (ending.length() < 2) {
            return "The email should end with something like .com or .ph";
        }
        for (int i = 0; i < ending.length(); i++) {
            if (!isLetter(ending.charAt(i))) {
                return "The email should end with letters, like .com or .ph";
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