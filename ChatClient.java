// ChatClient.java
// Simple terminal client for the chat server.

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server host (default: localhost): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        System.out.print("Enter server port (default: 12345): ");
        String portStr = scanner.nextLine().trim();
        int port = 12345;
        if (!portStr.isEmpty()) {
            port = Integer.parseInt(portStr);
        }

        try {
            Socket socket = new Socket(host, port);
            System.out.println("[CLIENT] Connected to " + host + ":" + port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] Disconnected from server.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            while (true) {
                // Coarse typing indicator: mark typing before entering a line
                out.println("/typing");

                String userInput = scanner.nextLine();
                if (userInput == null) break;
                userInput = userInput.trim();

                if (userInput.isEmpty()) {
                    out.println("/stoppedtyping");
                    continue;
                }

                out.println(userInput);
                out.println("/stoppedtyping");

                if (userInput.equalsIgnoreCase("/quit")) {
                    System.out.println("[CLIENT] Closing connection.");
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("[CLIENT ERROR] " + e.getMessage());
        }
    }
}
