// UserManager.java
// Handles loading and saving user accounts from/to "users.txt".
// Responsibilities:
//  - Read users.txt at startup
//  - Ensure at least one admin exists (default admin:admin if needed)
//  - Create new users (signup)
//  - Change passwords (/changepw)
//  - Rename users (/rename)
//
// File format accepted:
//   username:password
//   username:password:admin      // admin user
// Passwords:
//   - Legacy: plain-text
//   - New/changed: "$sha256$" + hex(SHA-256 hash)

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private static final String HASH_PREFIX = "$sha256$";

    private final String filePath;                // Path to users.txt
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserManager(String filePath) {
        this.filePath = filePath;
        reload();  // Load users immediately when constructed.
    }

    // Reload users from disk (used at startup and from menu "Reload users.txt").
    public synchronized void reload() {
        users.clear();
        File f = new File(filePath);

        // If the file doesn't exist, create an empty one.
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("[UserManager] Could not create " + filePath + ": " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Ignore empty lines or comments
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String pw = parts[1];
                    boolean admin = false;
                    if (parts.length >= 3 && "admin".equalsIgnoreCase(parts[2])) {
                        admin = true;
                    }
                    users.put(username, new User(username, pw, admin));
                }
            }
        } catch (IOException e) {
            System.out.println("[UserManager] Error reading users.txt: " + e.getMessage());
        }
    }

    // Ensure at least one admin exists.
    // If not, create a default admin account "admin:admin:admin".
    public synchronized void ensureDefaultAdmin() {
        boolean hasAdmin = users.values().stream().anyMatch(u -> u.isAdmin);
        if (!hasAdmin) {
            System.out.println("[UserManager] No admin found. Creating default admin 'admin' with password 'admin'.");
            String storedPw = HASH_PREFIX + hashPassword("admin");
            users.put("admin", new User("admin", storedPw, true));
            saveAll();
        }
    }

    // Fetch a user by username, or null if not found.
    public synchronized User getUser(String username) {
        return users.get(username);
    }

    // Verify a candidate password against a stored user's password.
    // Supports both legacy plaintext and hashed formats.
    public synchronized boolean verifyPassword(User user, String candidate) {
        if (user == null) return false;
        String stored = user.password;
        if (stored == null) return false;

        if (stored.startsWith(HASH_PREFIX)) {
            String storedHash = stored.substring(HASH_PREFIX.length());
            String candidateHash = hashPassword(candidate);
            return storedHash.equals(candidateHash);
        } else {
            // Legacy plaintext password
            return stored.equals(candidate);
        }
    }

    // Create a new user (used by signup).
    // Returns the new User or null if username already exists.
    public synchronized User createUser(String username, String password, boolean admin) {
        if (users.containsKey(username)) {
            return null;
        }
        String storedPw = HASH_PREFIX + hashPassword(password);
        User u = new User(username, storedPw, admin);
        users.put(username, u);
        saveAll();  // Persist the change to disk.
        return u;
    }

    // Change password for an existing user.
    public synchronized boolean changePassword(String username, String newPw) {
        User u = users.get(username);
        if (u == null) return false;
        u.password = HASH_PREFIX + hashPassword(newPw);
        saveAll();
        return true;
    }

    // Rename an existing user to a new name.
    // Updates in memory and writes to file.
    public synchronized boolean renameUser(String oldName, String newName) {
        if (!users.containsKey(oldName)) return false;
        if (users.containsKey(newName)) return false;  // Can't overwrite existing user

        User u = users.remove(oldName);
        u.username = newName;
        users.put(newName, u);
        saveAll();
        return true;
    }

    // Write all user accounts back to users.txt.
    // We rewrite the whole file every time to keep it simple.
    private synchronized void saveAll() {
        File f = new File(filePath);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (User u : users.values()) {
                if (u.isAdmin) {
                    pw.println(u.username + ":" + u.password + ":admin");
                } else {
                    pw.println(u.username + ":" + u.password);
                }
            }
        } catch (IOException e) {
            System.out.println("[UserManager] Error writing users.txt: " + e.getMessage());
        }
    }

    // Compute SHA-256 hash as hex string.
    private static String hashPassword(String pw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
