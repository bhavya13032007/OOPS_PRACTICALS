import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentService service = new StudentService();

        while (true) {
            System.out.println("1. Add Student");
            System.out.println("2. Show Count");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter name: ");
                String name = sc.nextLine();
                service.addStudent(new Student(name));
                System.out.println("Student added");
            } else if (choice == 2) {
                System.out.println("Total students: " + service.getCount());
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }

        sc.close();
    }
}