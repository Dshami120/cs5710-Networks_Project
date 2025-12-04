// ClientHandler.java
// One instance per connected client.

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final ChatServer server;
    final Socket socket;                 // package-visible for logging in /kick
    private BufferedReader in;
    private PrintWriter out;

    String username;                     // Set after successful login
    private boolean isAdmin;
    boolean isTyping;

    public ClientHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public String getRemoteAddress() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Welcome to the Java Chat Server.");
            out.println("Please log in.");

            if (!handleLogin()) {
                return;
            }

            server.sendHistoryTo(this);

            server.registerClient(username, this);

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (isTyping) {
                    isTyping = false;
                    String stopMsg = "[SYSTEM] " + username + " stopped typing.";
                    server.broadcastTypingMessage(stopMsg);
                }

                if (line.startsWith("/")) {
                    handleCommand(line);
                } else {
                    String formatted = username + ": " + line;
                    server.broadcast(formatted, username, "*", MessageType.BROADCAST, true);
                }
            }
        } catch (IOException e) {
            // Treat as disconnect.
        } finally {
            if (username != null) {
                server.getLogger().logConnection(username, socket, "DISCONNECT");
                server.removeClient(username);
            }
            closeSocket();
        }
    }

    // Login & signup logic.
    private boolean handleLogin() throws IOException {
        UserManager um = server.getUserManager();
        int attempts = 0;

        while (attempts < 3) {
            out.print("Username: ");
            out.flush();
            String user = in.readLine();
            if (user == null) return false;
            user = user.trim();

            User existing = um.getUser(user);

            if (existing != null) {
                out.print("Password: ");
                out.flush();
                String pw = in.readLine();
                if (pw == null) return false;
                pw = pw.trim();

                if (um.verifyPassword(existing, pw)) {
                    this.username = existing.username;
                    this.isAdmin = existing.isAdmin;
                    out.println("[SYSTEM] Login successful. Welcome, " + username + ".");
                    server.getLogger().logConnection(username, socket, "LOGIN_SUCCESS");
                    return true;
                } else {
                    attempts++;
                    out.println("[SYSTEM] Wrong password. Attempts: " + attempts + "/3");
                    server.getLogger().logConnection(user, socket, "LOGIN_FAIL");
                }
            } else {
                out.println("[SYSTEM] Username not found. Do you want to sign up? (yes/no)");
                String ans = in.readLine();
                if (ans == null) return false;
                ans = ans.trim().toLowerCase();

                if (ans.equals("yes") || ans.equals("y")) {
                    out.print("Create password: ");
                    out.flush();
                    String pw1 = in.readLine();
                    if (pw1 == null) return false;
                    out.print("Confirm password: ");
                    out.flush();
                    String pw2 = in.readLine();
                    if (pw2 == null) return false;

                    if (!pw1.equals(pw2)) {
                        out.println("[SYSTEM] Passwords do not match. Signup failed.");
                        return false;
                    }

                    User newUser = um.createUser(user, pw1, false);
                    if (newUser == null) {
                        out.println("[SYSTEM] Signup failed: user already exists.");
                        return false;
                    }
                    this.username = newUser.username;
                    this.isAdmin = newUser.isAdmin;
                    out.println("[SYSTEM] Signup successful. Welcome, " + username + ".");
                    server.getLogger().logConnection(username, socket, "SIGNUP_SUCCESS");
                    return true;
                } else {
                    out.println("[SYSTEM] Signup declined. Disconnecting.");
                    return false;
                }
            }
        }

        out.println("[SYSTEM] Too many failed attempts. Disconnecting.");
        return false;
    }

    private void handleCommand(String line) {
        try {
            if (line.equals("/typing")) {
                handleTypingStart();
                return;
            }

            if (line.equals("/stoppedtyping")) {
                handleTypingStop();
                return;
            }

            if (line.startsWith("/pm ")) {
                handlePrivateMessage(line);
                return;
            }

            if (line.startsWith("/askgpt ")) {
                handleAskGpt(line);
                return;
            }

            if (line.equals("/list")) {
                handleListUsers();
                return;
            }

            if (line.startsWith("/announce ")) {
                handleAnnounce(line);
                return;
            }

            if (line.startsWith("/kick ")) {
                handleKick(line);
                return;
            }

            if (line.startsWith("/changepw ")) {
                handleChangePw(line);
                return;
            }

            if (line.startsWith("/rename ")) {
                handleRename(line);
                return;
            }

            if (line.equals("/exit-server")) {
                handleExitServer();
                return;
            }

            out.println("[SYSTEM] Unknown command: " + line);
        } catch (Exception e) {
            out.println("[SYSTEM] Command error: " + e.getMessage());
        }
    }

    private void handleTypingStart() {
        if (!isTyping) {
            isTyping = true;
            String msg = "[SYSTEM] " + username + " is typing...";
            server.broadcastTypingMessage(msg);
        }
    }

    private void handleTypingStop() {
        if (isTyping) {
            isTyping = false;
            String msg = "[SYSTEM] " + username + " stopped typing.";
            server.broadcastTypingMessage(msg);
        }
    }

    private void handlePrivateMessage(String line) {
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 3) {
            out.println("[SYSTEM] Usage: /pm <user> <message>");
            return;
        }
        String targetName = parts[1];
        String msg = parts[2];

        ClientHandler target = server.getClient(targetName);
        if (target == null) {
            out.println("[SYSTEM] User not found or not online: " + targetName);
            return;
        }

        String toTarget = "[PM from " + username + "] " + msg;
        String toSender = "[PM to " + targetName + "] " + msg;

        target.sendMessage(toTarget);
        out.println(toSender);

        server.getLogger().logChat(username, targetName, MessageType.PRIVATE, toTarget);
        server.addToHistory(toTarget);
    }

    private void handleAskGpt(String line) {
        String prompt = line.substring("/askgpt ".length()).trim();
        if (prompt.isEmpty()) {
            out.println("[SYSTEM] Usage: /askgpt <prompt>");
            return;
        }

        out.println("[AI] Working on your request...");

        new Thread(() -> {
            try {
                String response = server.getAiClient().askGpt(prompt);
                String formatted = "[AI] " + response;

                out.println(formatted);

                server.getLogger().logChat("AI", username, MessageType.AI, formatted);
                server.addToHistory(formatted);
            } catch (Exception e) {
                String err = "[AI ERROR] " + e.getMessage();
                out.println(err);
            }
        }, "AIThread-" + username).start();
    }

    private void handleListUsers() {
        boolean adminView = isAdmin;
        var list = server.getActiveConnectionsInfo(adminView);
        out.println("[SYSTEM] Active users:");
        for (String s : list) {
            out.println(" - " + s);
        }
    }

    private void handleAnnounce(String line) {
        if (!isAdmin) {
            out.println("[SYSTEM] Only admins can use /announce.");
            return;
        }
        String msg = line.substring("/announce ".length()).trim();
        if (msg.isEmpty()) {
            out.println("[SYSTEM] Usage: /announce <message>");
            return;
        }
        String formatted = "[ADMIN] " + msg;
        server.broadcast(formatted, username, "*", MessageType.ADMIN, true);
        server.getLogger().logAdminAction(username, "ANNOUNCE");
    }

    private void handleKick(String line) {
        if (!isAdmin) {
            out.println("[SYSTEM] Only admins can use /kick.");
            return;
        }
        String targetName = line.substring("/kick ".length()).trim();
        if (targetName.isEmpty()) {
            out.println("[SYSTEM] Usage: /kick <user>");
            return;
        }

        ClientHandler target = server.getClient(targetName);
        if (target == null) {
            out.println("[SYSTEM] User not found or not online: " + targetName);
            return;
        }

        if (target.isTyping) {
            target.isTyping = false;
            server.broadcastTypingMessage("[SYSTEM] " + targetName + " stopped typing.");
        }

        target.sendMessage("[SYSTEM] You have been kicked by admin " + username + ".");

        // Log admin action and disconnect event BEFORE closing socket/rewiring maps.
        server.getLogger().logAdminAction(username, "KICK " + targetName);
        server.getLogger().logConnection(targetName, target.socket, "DISCONNECT");

        // Remove without broadcasting default "left chat" message.
        server.removeClient(targetName, false);
        target.closeSocket();

        String msg = "[SYSTEM] " + targetName + " was kicked by admin " + username + ".";
        server.broadcast(msg, "SYSTEM", "*", MessageType.SYSTEM, true);
    }

    private void handleChangePw(String line) {
        if (!isAdmin) {
            out.println("[SYSTEM] Only admins can use /changepw.");
            return;
        }
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 3) {
            out.println("[SYSTEM] Usage: /changepw <user> <newpw>");
            return;
        }
        String targetUser = parts[1];
        String newPw = parts[2];

        boolean ok = server.getUserManager().changePassword(targetUser, newPw);
        if (!ok) {
            out.println("[SYSTEM] Failed: user not found.");
        } else {
            out.println("[SYSTEM] Password updated for " + targetUser + ".");
            server.getLogger().logAdminAction(username, "CHANGE_PW " + targetUser);
        }
    }

    private void handleRename(String line) {
        if (!isAdmin) {
            out.println("[SYSTEM] Only admins can use /rename.");
            return;
        }
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 3) {
            out.println("[SYSTEM] Usage: /rename <old> <new>");
            return;
        }
        String oldName = parts[1];
        String newName = parts[2];

        boolean ok = server.getUserManager().renameUser(oldName, newName);
        if (!ok) {
            out.println("[SYSTEM] Failed: user " + oldName + " not found or new name already exists.");
            return;
        }

        ClientHandler target = server.getClient(oldName);
        if (target != null) {
            // Remove from map silently and re-register under new name.
            server.removeClient(oldName, false);
            target.username = newName;
            server.registerClient(newName, target);
            // Note: registerClient() will broadcast a "joined" message,
            // but we do NOT want a "left" message for oldName.
            // We've suppressed that via removeClient(oldName, false).

            if (target.isTyping) {
                target.isTyping = false;
                server.broadcastTypingMessage("[SYSTEM] " + oldName + " stopped typing.");
            }
        }

        String msg = "[SYSTEM] User " + oldName + " has been renamed to " + newName + " by admin " + username + ".";
        server.broadcast(msg, "SYSTEM", "*", MessageType.SYSTEM, true);
        server.getLogger().logAdminAction(username, "RENAME " + oldName + " -> " + newName);
    }

    private void handleExitServer() {
        if (!isAdmin) {
            out.println("[SYSTEM] Only admins can use /exit-server.");
            return;
        }
        server.getLogger().logAdminAction(username, "EXIT_SERVER");
        server.shutdownFromAdmin();
    }
}
