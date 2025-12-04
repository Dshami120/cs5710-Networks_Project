// ChatServer.java
// Core server logic.

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {

    private int port = 12345;  // default
    private volatile boolean running = false;

    private ServerSocket serverSocket;
    private Thread acceptThread;

    // Thread pool for handling clients (one thread per client).
    // NOTE: We never shut this down in stop(), so the server can be started again.
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private final UserManager userManager = new UserManager("users.txt");

    private final ChatLogger logger = new ChatLogger();

    private final Deque<String> historyBuffer = new ArrayDeque<>();
    private final Object historyLock = new Object();
    private static final int HISTORY_LIMIT = 1000;

    private final AIClient aiClient = new AIClient();

    public ChatLogger getLogger() {
        return logger;
    }

    public AIClient getAiClient() {
        return aiClient;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    // Start the server on the current port.
    public void start() {
        if (running) {
            System.out.println("[SERVER] Already running.");
            return;
        }

        try {
            // Ensure at least one admin exists before accepting connections.
            userManager.ensureDefaultAdmin();

            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("[SERVER] Started on port " + port);

            acceptThread = new Thread(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        logger.logConnection("-", socket, "CONNECT");
                        ClientHandler handler = new ClientHandler(this, socket);
                        clientPool.submit(handler);
                    } catch (IOException e) {
                        if (running) {
                            System.out.println("[SERVER] Error accepting connection: " + e.getMessage());
                        }
                    }
                }
            }, "AcceptThread");
            acceptThread.start();
        } catch (IOException e) {
            System.out.println("[SERVER ERROR] Could not start server: " + e.getMessage());
            running = false;
        }
    }

    // Stop the server and disconnect all clients.
    // If fromExitCommand is true, we log SERVER_SHUTDOWN.
    public void stop(boolean fromExitCommand) {
        if (!running) {
            System.out.println("[SERVER] Already stopped.");
            return;
        }

        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        if (acceptThread != null && acceptThread.isAlive()) {
            try {
                acceptThread.join(2000);
            } catch (InterruptedException ignored) {}
        }

        // Notify and disconnect all clients.
        for (ClientHandler ch : clients.values()) {
            try {
                ch.sendMessage("[SYSTEM] Server shutting down.");
                ch.closeSocket();
            } catch (Exception ignored) {}
        }
        clients.clear();

        if (fromExitCommand) {
            logger.logServerShutdown();
        }

        System.out.println("[SERVER] Stopped.");
    }

    // Reload users.txt from disk.
    public void reloadUsers() {
        userManager.reload();
        userManager.ensureDefaultAdmin();
    }

    // Register a client after successful login.
    public void registerClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        int count = clients.size();
        String msg = "[SYSTEM] " + username + " joined the chat. (Online: " + count + ")";
        System.out.println(msg);
        logAndBroadcastSystemMessage(msg);
    }

    // Default behavior: broadcast that user left.
    public void removeClient(String username) {
        removeClient(username, true);
    }

    // Remove a client (on disconnect or kick/rename).
    // broadcast=false -> do not send join/leave system messages.
    public void removeClient(String username, boolean broadcast) {
        if (username == null) return;
        ClientHandler removed = clients.remove(username);
        if (!broadcast || removed == null) {
            return;
        }
        int count = clients.size();
        String msg = "[SYSTEM] " + username + " left the chat. (Online: " + count + ")";
        System.out.println(msg);
        logAndBroadcastSystemMessage(msg);
    }

    // Retrieve a ClientHandler by username (used by /pm and /kick).
    public ClientHandler getClient(String username) {
        return clients.get(username);
    }

    // Broadcast a message to all clients and optionally log/history it.
    public void broadcast(String formatted, String fromUser, String toUser, MessageType type, boolean logAndHistory) {
        for (ClientHandler ch : clients.values()) {
            ch.sendMessage(formatted);
        }

        if (logAndHistory) {
            logger.logChat(fromUser, toUser, type, formatted);
            addToHistory(formatted);
        }
    }

    // Broadcast typing indicator (SYSTEM text) WITHOUT logging or history.
    public void broadcastTypingMessage(String formatted) {
        System.out.println(formatted);
        for (ClientHandler ch : clients.values()) {
            ch.sendMessage(formatted);
        }
    }

    // Helper for join/leave/system events that MUST be logged and saved in history.
    public void logAndBroadcastSystemMessage(String msg) {
        logger.logChat("SYSTEM", "*", MessageType.SYSTEM, msg);
        addToHistory(msg);
        for (ClientHandler ch : clients.values()) {
            ch.sendMessage(msg);
        }
    }

    // Add one line of text to the history buffer.
    public void addToHistory(String msg) {
        synchronized (historyLock) {
            if (historyBuffer.size() >= HISTORY_LIMIT) {
                historyBuffer.removeFirst();
            }
            historyBuffer.addLast(msg);
        }
    }

    // Send last N messages to a client after login.
    public void sendHistoryTo(ClientHandler ch) {
        ch.sendMessage("=== Last " + HISTORY_LIMIT + " Messages ===");
        synchronized (historyLock) {
            for (String s : historyBuffer) {
                ch.sendMessage(s);
            }
        }
        ch.sendMessage("=== End of History ===");
    }

    // Used by menu and /list to show active connections.
    public void printActiveConnections() {
        if (clients.isEmpty()) {
            System.out.println("[SERVER] No active connections.");
            return;
        }
        System.out.println("[SERVER] Active connections:");
        for (ClientHandler ch : clients.values()) {
            System.out.println(" - " + ch.getUsername() + " | " + ch.getRemoteAddress());
        }
    }

    // Build a list of active connections for /list command.
    public List<String> getActiveConnectionsInfo(boolean adminView) {
        List<String> res = new ArrayList<>();
        for (ClientHandler ch : clients.values()) {
            if (adminView) {
                res.add(ch.getUsername() + " | " + ch.getRemoteAddress());
            } else {
                res.add(ch.getUsername());
            }
        }
        return res;
    }

    // Called by an admin's /exit-server command.
    public void shutdownFromAdmin() {
        stop(true);  // fromExitCommand = true -> log SERVER_SHUTDOWN
    }
}
