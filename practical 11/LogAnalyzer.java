import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class LogAnalyzer {
    public static void main(String[] args) throws IOException {
        Files.write(Paths.get("log1.txt"), Arrays.asList(
            "User login successful",
            "Error occurred",
            "User logout"
        ));

        Files.write(Paths.get("log2.txt"), Arrays.asList(
            "Error in connection",
            "User login successful"
        ));

        String keyword = "error";
        int totalLines = 0;
        int matches = 0;

        String[] files = {"log1.txt", "log2.txt"};

        for (String file : files) {
            Path path = Paths.get(file);
            long size = Files.size(path);

            System.out.println("File: " + file);
            System.out.println("Size: " + size + " bytes");

            try (var lines = Files.lines(path)) {
                Iterator<String> it = lines.iterator();

                while (it.hasNext()) {
                    String line = it.next();
                    totalLines++;

                    if (line.toLowerCase().contains(keyword.toLowerCase())) {
                        matches++;
                    }
                }
            }
        }

        System.out.println("Total lines: " + totalLines);
        System.out.println("Keyword matches: " + matches);
    }
}