package group07.group.allocation.validator;

import java.util.regex.Pattern;

public class EmailValidation {

    public static boolean patternMatches(String emailAddress, String regex){
        return Pattern.compile(regex).matcher(emailAddress).matches();
    }

    public static boolean patternMatches(String emailAddress){
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@"
                + "[^-][A-Za-z0-9+-]+(\\.[A-Za-z0-9+-]+)*(\\.[A-Za-z]{2,})$";
        return !Pattern.compile(regexPattern).matcher(emailAddress).matches();
    }
}
