# 📡 Java Socket Chat Server — Final README  
Secure • Multi-User • Admin Controls • AI Integration • Logging • History Buffer

A fully-featured, production-style multi-client chat server built entirely with **pure Java Sockets**.  
This system includes authentication, admin tools, OpenAI GPT integration, logging, message history, typing indicators, and a full operator console.  
Perfect for **Networking**, **Operating Systems**, **Software Engineering**, and **Capstone Projects**.

---

# 📚 Table of Contents
1. 🚀 Overview  
2. 📋 Requirements Specification  
3. 🧭 System Capabilities  
4. ⛔ System Limitations  
5. 📁 Project Structure  
6. 🔧 Feature Breakdown  
7. 🌐 Client Commands & Usage  
8. 🧾 Logging System  
9. 🖥 Server Console (Menu)  
10. 🔒 Security Notes  
11. 🗄 Required Files & Setup  
12. 🧪 Example Session  
13. 🔮 Future Extensions  
14. 🏁 Conclusion  

---

# 🚀 Overview

## 📡 Java Socket Chat Server  
Secure • Multi-User • Admin Controls • AI Integration • Full Logging • History Buffer

This project implements a robust TCP chat server in Java with:

- 🔐 Secure password-based authentication with SHA‑256 hashing  
- 💬 Broadcast + private /pm messages  
- 🤖 OpenAI GPT integration with `/askgpt`  
- 🛡 Admin commands for moderation & server control  
- 🧾 Complete CSV logging for all chat and connection events  
- 🧠 Rolling 1000-message history buffer  
- 🖥 Interactive server console to manage the server  

---

# 📋 Requirements Specification Sheet

## ✅ 1.1 Functional Requirements

### 🧑‍💻 User Accounts & Authentication
The system shall:
- Allow users to log in using username + password  
- Store passwords using **SHA‑256 hashing**  
- Support signup when username does not exist  
- Auto-create a default admin account (`admin/admin`) if no admin exists  
- Persist admin roles inside `users.txt`  
- Lock out users after **3 failed login attempts**  
- Support both plaintext + hashed legacy entries, automatically upgrading them  

---

### 💬 Messaging & Chat Features
The system shall:
- Provide broadcast global chat messaging  
- Provide private messaging using:

```
/pm <user> <message>
```

- Announce users joining/leaving  
- Maintain and replay the **last 1000 messages**  
- Support typing indicators:

```
/typing
/stoppedtyping
```

---

### 🛡 Admin Tools
Admins shall be able to:

| Command | Description |
|--------|-------------|
| `/kick <user>` | Disconnect a user |
| `/changepw <user> <pw>` | Change user password |
| `/rename <old> <new>` | Live rename user & update users.txt |
| `/announce <msg>` | Global admin broadcast |
| `/exit-server` | Shut the server down |

Admins also see IP:port on `/list`.

---

### 🤖 AI Integration
The system shall allow users to query OpenAI GPT using:

```
/askgpt <prompt>
```

- Sends prompt to OpenAI  
- Streams response back  
- Logs as AI messages  
- Requires environment variable `OPENAI_API_KEY`  

---

### 🖥 Server Console (Menu)
The server operator shall have an interactive runtime console allowing:

- Starting/stopping the server  
- Changing the listening port  
- Viewing active connections  
- Showing log file paths  
- Reloading users.txt  
- Exiting the server application  

---

### 🧾 Logging Requirements
The system shall log to:

### **`chat_history.csv`**  
Columns:  
- timestamp  
- from_user  
- to_user  
- message_type  
- message  

Message types: BROADCAST, PM, SYSTEM, ADMIN, AI, etc.

---

### **`connections.csv`**  
Columns:  
- timestamp  
- username  
- ip  
- port  
- event_type  

Event types:  
CONNECT, LOGIN_SUCCESS, LOGIN_FAIL, SIGNUP_SUCCESS, DISCONNECT, ADMIN_ACTION, SERVER_SHUTDOWN.

---

## ⚙️ 1.2 Non-Functional Requirements

| Category | Requirement |
|---------|-------------|
| ⚡ Performance | Support multiple concurrent clients with low latency |
| 💪 Reliability | Auto-create folders/files; handle disconnects |
| 🔐 Security | SHA‑256 hashing, environment variable for API keys |
| 🙂 Usability | Intuitive CLI client, clear feedback messages |
| 🖥 Compatibility | Java 17+, any OS |

---

# 🧭 System Capabilities (What the System *Does*)
✔ Multi-client real-time text chat  
✔ Secure authentication with hashing  
✔ Broadcast + private messaging  
✔ Typing indicators  
✔ OpenAI GPT queries  
✔ Admin tools for moderation  
✔ CSV logging  
✔ 1000-message history buffer  
✔ Live user renaming  
✔ Graceful disconnect handling  
✔ Fully interactive server control menu  

---

# ⛔ System Limitations (What the System *Does Not* Do)
Useful for professors & evaluators.

❌ No TLS/SSL encrypted sockets  
❌ No GUI client (terminal only)  
❌ No file transfer  
❌ No spam/rate limiting  
❌ No persistent database  
❌ No multi-room channels  
❌ No permanent banning system  
❌ No email/password recovery  
❌ No web admin dashboard  

---

# 📁 Project Structure

```
ChatServerProject/
│
├── ServerMain.java        # Entry point with interactive console menu
├── ChatServer.java        # Core server logic, accept loop, broadcasting
├── ClientHandler.java     # Per-client thread handler
├── ChatClient.java        # Terminal-based client
│
├── User.java              # User model
├── UserManager.java       # Handles users.txt, hashing, roles
│
├── ChatLogger.java        # CSV logging for chat + connections
├── ServerUtils.java       # Utility helpers (timestamps, CSV escape)
├── MessageType.java       # Message type enum
├── AIClient.java          # Wrapper around OpenAI GPT
│
├── users.txt              # User accounts file
└── logs/
    ├── chat_history.csv
    └── connections.csv
```

---

# 🔧 Feature Breakdown

## 1. 🔐 Authentication System
- Supports plaintext & SHA‑256 hashed users  
- Upgrades old accounts on password change  
- Auto-creates admin if needed  
- Signup flow included  
- 3-strike lockout behavior  

---

## 2. 💬 Messaging System
### Broadcast:
```
username: message
```

### Private Messages:
Sender sees:
```
[PM to bob] hello
```
Receiver sees:
```
[PM from alice] hello
```

### System Messages:
```
[SYSTEM] alice has joined.
[SYSTEM] alice has left.
```

---

## 3. 🧠 History Buffer (1000 messages)
Automatically sent after login:

```
=== Last 1000 Messages ===
...
=== End of History ===
```

---

## 4. ⌨ Typing Indicators

```
/typing
/stoppedtyping
```

---

## 5. 🤖 AI Integration

Example usage:

```
/askgpt explain polymorphism
```

Returns streamed GPT output:

```
[AI] Polymorphism in OOP allows...
```

---

## 6. 🛡 Admin Commands

| Command | Description |
|--------|-------------|
| `/kick <user>` | Disconnects the user |
| `/changepw <user> <pw>` | Resets user password |
| `/rename <old> <new>` | Renames user in live session & file |
| `/announce <msg>` | Global admin announcement |
| `/exit-server` | Shuts down entire server |

---

# 🌐 Client Usage & Commands

## 🔧 Running the Program

### Compile:
```
javac *.java
```

### Start server:
```
java ServerMain
```

### Start client:
```
java ChatClient
```

Defaults:
```
host: localhost
port: 12345
```

---

# 💻 Command Reference

| Command | Description |
|--------|-------------|
| `/pm <user> <msg>` | Send private message |
| `/typing` | Show typing indicator |
| `/stoppedtyping` | Remove typing indicator |
| `/askgpt <prompt>` | Ask GPT a question |
| `/list` | Show connected users |
| `/announce <msg>` | Admin announcement |
| `/kick <user>` | Kick user |
| `/changepw <user> <pw>` | Change password |
| `/rename <old> <new>` | Rename a user |
| `/exit-server` | Shutdown server |

---

# 🧾 Logging System

## `chat_history.csv`

| Column | Meaning |
|--------|---------|
| timestamp | ISO timestamp |
| from_user | sender |
| to_user | receiver or ALL |
| message_type | BROADCAST, PM, SYSTEM, ADMIN, AI |
| message | Content |

---

## `connections.csv`

| Column | Meaning |
|--------|---------|
| timestamp | Event time |
| username | User account |
| ip | IP address |
| port | Port |
| event_type | CONNECT, LOGIN_SUCCESS, ADMIN_ACTION, etc.|

Includes server shutdown entries.

---

# 🖥 Server Console Menu

```
1. Start server
2. Stop server
3. Set listening port
4. View active connections
5. Show path to chat_history.csv
6. Show path to connections.csv
7. Reload users.txt
8. Exit application
```

---

# 🔒 Security Notes

- All passwords hashed using SHA‑256  
- OpenAI key stored in environment variables  
- No sensitive data stored in logs  
- CSV fields properly escaped for safety  

---

# 🗄 Required Files & Setup

- `users.txt` must exist (auto-created if missing)  
- `logs/` folder auto-created  
- Required env variable:

### Windows
```
setx OPENAI_API_KEY "yourkey"
```

### macOS/Linux
```
export OPENAI_API_KEY="yourkey"
```

---

# 🧪 Example Session

```
Username: bob
Password: ****

[SYSTEM] Login successful.
=== Last 1000 Messages ===
...
=== End of History ===

bob: hello everyone!
```

Admin kicks a user:

```
/kick bob
[SYSTEM] bob was kicked by admin
```

AI Example:
```
/askgpt write a haiku about Java sockets
```

---

# 🔮 Future Extensions

- 🖼 GUI Client (JavaFX/Swing)  
- 📁 File transfer between users  
- 🧱 MySQL authentication backend  
- 🔐 TLS encrypted sockets  
- 🌐 Web admin dashboard  
- 🚫 Spam/rate limiting  
- 📡 Multi-room channels  

---

# 🏁 Conclusion

This project provides a **true production-style socket chat system** with strong documentation, modern features, and clean architecture.  
Suitable for classroom submission, portfolio use, and real technical demonstrations.

