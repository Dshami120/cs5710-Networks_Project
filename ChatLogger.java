// ChatLogger.java
// Handles:
//   - Writing chat messages to logs/chat_history.csv
//   - Writing connection events to logs/connections.csv
//   - Logging admin actions and server shutdown

import java.io.*;
import java.nio.file.*;

public class ChatLogger {

    private final Path logsDir;
    private final Path chatHistoryPath;
    private final Path connectionsPath;

    public ChatLogger() {
        this.logsDir = Paths.get("logs");

        // Create "logs" directory if it doesn't exist.
        try {
            Files.createDirectories(logsDir);
        } catch (IOException e) {
            System.out.println("[ChatLogger] Could not create logs directory: " + e.getMessage());
        }

        this.chatHistoryPath = logsDir.resolve("chat_history.csv");
        this.connectionsPath = logsDir.resolve("connections.csv");

        // Initialize files with headers if they don't exist.
        initFile(chatHistoryPath, "timestamp,from_user,to_user,message_type,message");
        initFile(connectionsPath, "timestamp,username,ip,port,event_type");
    }

    // Create file with header line if it is missing.
    private void initFile(Path path, String header) {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(header);
                }
            }
        } catch (IOException e) {
            System.out.println("[ChatLogger] Error initializing " + path + ": " + e.getMessage());
        }
    }

    public String getChatLogPath() {
        return chatHistoryPath.toAbsolutePath().toString();
    }

    public String getConnectionsLogPath() {
        return connectionsPath.toAbsolutePath().toString();
    }

    // Log a chat message of any type (BROADCAST, PRIVATE, SYSTEM, ADMIN, AI).
    public synchronized void logChat(String fromUser, String toUser, MessageType type, String message) {
        String ts = ServerUtils.now();
        String safeMsg = ServerUtils.escapeForCsv(message);
        String line = String.format("%s,%s,%s,%s,\"%s\"",
                ts, fromUser, toUser, type.name(), safeMsg);
        try (PrintWriter pw = new PrintWriter(new FileWriter(chatHistoryPath.toFile(), true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("[ChatLogger] Error writing chat log: " + e.getMessage());
        }
    }

    // Log connection or authentication events involving a specific socket.
    public synchronized void logConnection(String username, java.net.Socket socket, String eventType) {
        String ts = ServerUtils.now();
        String ip = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        String line = String.format("%s,%s,%s,%d,%s", ts, username, ip, port, eventType);
        try (PrintWriter pw = new PrintWriter(new FileWriter(connectionsPath.toFile(), true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("[ChatLogger] Error writing connections log: " + e.getMessage());
        }
    }

    // Log server-wide events without socket details, e.g., SERVER_SHUTDOWN.
    public synchronized void logServerShutdown() {
        String ts = ServerUtils.now();
        String line = String.format("%s,%s,%s,%d,%s", ts, "-", "-", 0, "SERVER_SHUTDOWN");
        try (PrintWriter pw = new PrintWriter(new FileWriter(connectionsPath.toFile(), true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("[ChatLogger] Error writing SERVER_SHUTDOWN: " + e.getMessage());
        }
    }

    // Log admin actions like KICK, RENAME, CHANGE_PW, EXIT_SERVER.
    public synchronized void logAdminAction(String adminUsername, String action) {
        String ts = ServerUtils.now();
        String line = String.format("%s,%s,%s,%d,%s", ts, adminUsername, "-", 0, "ADMIN_ACTION:" + action);
        try (PrintWriter pw = new PrintWriter(new FileWriter(connectionsPath.toFile(), true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("[ChatLogger] Error writing ADMIN_ACTION: " + e.getMessage());
        }
    }
}
