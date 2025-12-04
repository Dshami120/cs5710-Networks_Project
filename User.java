// User.java
// Simple data holder for a user account loaded from users.txt.

public class User {
    // Username used to log in and identify the user in chat.
    String username;

    // Stored password:
    //  - Either plain-text (legacy) OR
    //  - "$sha256$" + hex(SHA-256 hash) for new/changed passwords.
    String password;

    // Flag that indicates if this user has admin privileges.
    boolean isAdmin;

    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}
