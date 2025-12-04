// ServerUtils.java
// Small helper functions shared by logger and other classes.

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerUtils {

    // Date-time formatter used for log timestamps.
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Returns current timestamp as "YYYY-MM-DD HH:MM:SS"
    public static String now() {
        return LocalDateTime.now().format(DTF);
    }

    // Very small CSV escape:
    // - Replace newlines with \n
    // - Replace " with '' to avoid breaking the CSV cell
    public static String escapeForCsv(String message) {
        if (message == null) return "";
        return message.replace("\r", "\\r")
                      .replace("\n", "\\n")
                      .replace("\"", "''");
    }
}
