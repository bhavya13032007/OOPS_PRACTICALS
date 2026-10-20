import java.util.*;

class DivideByZeroException extends Exception {
    DivideByZeroException(String message) {
        super(message);
    }
}

public class GuardedCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean success = false;

        while (!success) {
            try {
                System.out.print("Enter first number: ");
                double a = Double.parseDouble(sc.nextLine());

                System.out.print("Enter second number: ");
                double b = Double.parseDouble(sc.nextLine());

                System.out.print("Enter operator (+, -, *, /): ");
                char op = sc.nextLine().charAt(0);

                double result;

                if (op == '/' && b == 0)
                    throw new DivideByZeroException("Cannot divide by zero.");

                switch (op) {
                    case '+': result = a + b; break;
                    case '-': result = a - b; break;
                    case '*': result = a * b; break;
                    case '/': result = a / b; break;
                    default: throw new IllegalArgumentException("Invalid operator.");
                }

                System.out.println("Result: " + result);
                success = true;

            } catch (NumberFormatException e) {
                System.out.println("Invalid number input. Please enter valid numbers.");
            } catch (DivideByZeroException e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println("Attempt logged.");
            }
        }

        sc.close();
    }
}
