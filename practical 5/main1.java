abstract class employee {
    abstract void salary();

    void display() {
        System.out.println("This is an employee.");
    }
}
abstract class monthly extends employee {
    abstract void salary();
}
abstract class fulltime extends employee {
    abstract void salary();
}
abstract class parttime extends employee {
    abstract void salary();
}
abstract class intern extends employee {
    abstract void salary();
}
public class main1{
    public static void main(String[]args){
        
        employee e1 = new monthly() {
            void salary() {
                double salary = 5000;
                System.out.println("Monthly employee salary: " + salary);
            }
        };
        e1.display();
        e1.salary();

        employee e2 = new fulltime() {
            void salary() {
                double salary = 8000;
                System.out.println("Full-time employee salary: " + salary);
            }
        };
        e2.display();
        e2.salary();

        employee e3 = new parttime() {
            void salary() {
                double salary = 3000;
                System.out.println("Part-time employee salary: " + salary);
            }
        };
        e3.display();
        e3.salary();

        employee e4 = new intern() {
            void salary() {
                double salary = 2000;
                System.out.println("Intern employee salary: " + salary);
            }
        };
        e4.display();
        e4.salary();
    }
}
