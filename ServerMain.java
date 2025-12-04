// ServerMain.java
// Entry point for the server application.

import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatServer server = new ChatServer();

        while (true) {
            System.out.println("─────────────────────────────────────");
            System.out.println("  Java Socket Chat Server - MENU");
            System.out.println("─────────────────────────────────────");
            System.out.println("Port: " + server.getPort() + " | Running: " + server.isRunning());
            System.out.println("1. Start server");
            System.out.println("2. Stop server");
            System.out.println("3. Set listening port (before Start only)");
            System.out.println("4. View active connections (username, IP, port)");
            System.out.println("5. Show path to chat_history.csv");
            System.out.println("6. Show path to connections.csv");
            System.out.println("7. Reload users.txt");
            System.out.println("8. Exit application");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        server.start();
                        break;
                    case "2":
                        server.stop(false);
                        break;
                    case "3":
                        if (server.isRunning()) {
                            System.out.println("[MENU] Can't change port while server is running.");
                        } else {
                            System.out.print("Enter new port number: ");
                            String p = scanner.nextLine().trim();
                            int port = Integer.parseInt(p);
                            server.setPort(port);
                            System.out.println("[MENU] Port set to " + port);
                        }
                        break;
                    case "4":
                        server.printActiveConnections();
                        break;
                    case "5":
                        System.out.println("[MENU] chat_history.csv path: " + server.getLogger().getChatLogPath());
                        break;
                    case "6":
                        System.out.println("[MENU] connections.csv path: " + server.getLogger().getConnectionsLogPath());
                        break;
                    case "7":
                        server.reloadUsers();
                        System.out.println("[MENU] users.txt reloaded.");
                        break;
                    case "8":
                        if (server.isRunning()) {
                            server.stop(false);
                        }
                        System.out.println("[MENU] Exiting application.");
                        return;
                    default:
                        System.out.println("[MENU] Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[MENU ERROR] " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
