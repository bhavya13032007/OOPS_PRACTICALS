import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {
    @Test
    void testAddStudent() {
        StudentService service = new StudentService();

        service.addStudent(new Student("Bhavya"));

        assertEquals(1, service.getCount());
    }

    @Test
    void testAddMultipleStudents() {
        StudentService service = new StudentService();

        service.addStudent(new Student("Bhavya"));
        service.addStudent(new Student("Rahul"));

        assertEquals(2, service.getCount());
    }

    @Test
    void testInvalidStudent() {
        StudentService service = new StudentService();

        assertThrows(IllegalArgumentException.class, () -> {
            service.addStudent(null);
        });
    }
}