import java.io.*;

class Student implements Serializable {
    int id;
    String name;
    transient String password;

    Student(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}

public class SaveLoad {
    public static void main(String[] args) throws Exception {
        Student[] students = {
            new Student(1, "Bhavya", "abc123"),
            new Student(2, "Rahul", "xyz456")
        };

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("student.dat"));
        out.writeObject(students);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.dat"));
        Student[] loaded = (Student[]) in.readObject();
        in.close();

        for (Student s : loaded) {
            System.out.println(s.id + " " + s.name + " " + s.password);
        }
    }
}