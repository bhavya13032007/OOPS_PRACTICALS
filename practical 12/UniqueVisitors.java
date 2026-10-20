import java.util.*;

public class UniqueVisitors {
    public static void main(String[] args) {
        String[] logins = {
            "bhavya", "rahul", "amit", "bhavya",
            "priya", "rahul", "neha"
        };

        HashSet<String> set = new HashSet<>();
        LinkedHashSet<String> orderedSet = new LinkedHashSet<>();

        for (String name : logins) {
            set.add(name);
            orderedSet.add(name);
        }

        System.out.println("Unique visitors: " + set.size());

        System.out.println("First three distinct names:");

        int count = 0;
        for (String name : orderedSet) {
            if (count == 3)
                break;

            System.out.println(name);
            count++;
        }
    }
}