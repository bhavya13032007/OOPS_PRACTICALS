import java.util.List;

public class Driver {
    public static void main(String[] args) {

        SignupForm form = new SignupForm("", "abcdefghijklmnop");

        List<String> errors = Validator.check(form);

        for (String error : errors) {
            System.out.println(error);
        }
    }
}