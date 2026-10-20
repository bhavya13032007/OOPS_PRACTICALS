import java.util.ArrayList;

public class StudentService {
    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student s) {
        if (s == null)
            throw new IllegalArgumentException("Invalid student");

        students.add(s);
    }

    public int getCount() {
        return students.size();
    }

    public Student getStudent(int index) {
        if (index < 0 || index >= students.size())
            throw new IllegalArgumentException("Invalid index");

        return students.get(index);
    }
}