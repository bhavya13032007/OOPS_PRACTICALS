import java.lang.reflect.Method;

public class Driver1 {

    public static void main(String[] args) throws Exception {

        MyTests obj = new MyTests();

        Method[] methods = MyTests.class.getDeclaredMethods();

        int count = 0;

        for (Method m : methods) {

            if (m.isAnnotationPresent(Run.class)) {
                m.invoke(obj);
                count++;
            }
        }

        System.out.println("Methods ran: " + count);
    }
}