public class ChatFilter {
    public static void filter(String[] logs, String keyword) {
        StringBuilder report = new StringBuilder();
        int count = 0;

        for (String line : logs) {
            String[] parts = line.split(" ", 3);

            if (parts.length < 3)
                continue;

            if (parts[2].toLowerCase().contains(keyword.toLowerCase())) {
                count++;
                report.append(parts[0]).append(" ")
                      .append(parts[1]).append(": ")
                      .append(parts[2]).append("\n");
            }
        }

        System.out.println("Matches: " + count);
        System.out.println(report);
    }
}