public class PasswordChecker {
    public static String strength(String pw) {
        int count = 0;

        if (pw.matches(".{8,}")) {
            System.out.println("Length: Passed");
            count++;
        } else {
            System.out.println("Length: Failed");
        }

        if (pw.matches(".*[A-Z].*")) {
            System.out.println("Uppercase: Passed");
            count++;
        } else {
            System.out.println("Uppercase: Failed");
        }

        if (pw.matches(".*[0-9].*")) {
            System.out.println("Digit: Passed");
            count++;
        } else {
            System.out.println("Digit: Failed");
        }

        if (pw.matches(".*[^a-zA-Z0-9].*")) {
            System.out.println("Special Character: Passed");
            count++;
        } else {
            System.out.println("Special Character: Failed");
        }

        if (count <= 1)
            return "Weak";
        else if (count <= 3)
            return "Medium";
        else
            return "Strong";
    }
}