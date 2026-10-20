public class Driver {
    public static void main(String[] args) {
        String[] passwords = {"abc", "abcdef12", "Abcdef12", "Abcd1234!"};

        for (String pw : passwords) {
            System.out.println("\nPassword: " + pw);
            System.out.println("Strength: " + PasswordChecker.strength(pw));
        }
    }
}