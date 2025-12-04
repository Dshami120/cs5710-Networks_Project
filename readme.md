# 📡 Java Socket Chat Server
### Secure • Multi-User • Admin Controls • AI Integration • Logging • History Buffer

A fully-featured, production-style multi-client chat server built with **pure Java Sockets**, ideal for networking coursework, capstone projects, and real-world distributed systems practice.

This server supports authentication, admin management, real-time chat features, message history, OpenAI GPT integration, and robust logging.

---

## 🚀 Features

### 🔐 Authentication System
- Username/password login  
- **SHA-256 hashed passwords**  
- Automatic upgrade of legacy plaintext passwords  
- New user signup  
- Auto-creation of default admin account  
- Admin roles stored in `users.txt`  

---

### 💬 Chat Features
- Global broadcast chat  
- Private messaging: `/pm <user> <msg>`  
- Typing indicators  
- Join/leave announcements  
- **1000-message rolling history buffer**  
- Auto-formatted messages  
- Full chat logging  

---

### 🤖 AI Integration (OpenAI GPT)
Use:

```
/askgpt <prompt>
```

Server:
- Calls OpenAI GPT  
- Streams messages back to the requester  
- Logs as `AI` messages  
- Requires environment variable:

Windows:
```
setx OPENAI_API_KEY "yourkey"
```

Mac/Linux:
```
export OPENAI_API_KEY="yourkey"
```

---

### 🛡 Admin Tools
| Command | Description |
|---------|-------------|
| `/kick <user>` | Disconnect a user |
| `/changepw <user> <pw>` | Reset password |
| `/rename <old> <new>` | Rename user (live + file) |
| `/announce <msg>` | Global admin message |
| `/list` | Show users (admin sees IP:port) |
| `/exit-server` | Shutdown server |

---

## 🧾 Logging System

### `chat_history.csv`
Columns:
- timestamp  
- from_user  
- to_user  
- message_type  
- message  

Logs:
- Broadcast  
- PMs  
- Admin events  
- AI responses  
- System events  

### `connections.csv`
Columns:
- timestamp  
- username  
- ip  
- port  
- event_type  

Logs:
- CONNECT  
- LOGIN_SUCCESS  
- LOGIN_FAIL  
- SIGNUP_SUCCESS  
- DISCONNECT  
- ADMIN_ACTION  
- SERVER_SHUTDOWN  

---

## 📁 Project Structure
```
ChatServerProject/
│
├── ServerMain.java
├── ChatServer.java
├── ClientHandler.java
├── ChatClient.java
│
├── User.java
├── UserManager.java
│
├── ChatLogger.java
├── ServerUtils.java
├── MessageType.java
├── AIClient.java
│
├── users.txt
└── logs/
    ├── chat_history.csv
    └── connections.csv
```

---

## 🔧 Running the Server

### Compile
```
javac *.java
```

### Start server
```
java ServerMain
```

### Start client
```
java ChatClient
```

Defaults:
```
Host: localhost
Port: 12345
```

---

## 🌐 Client Commands

| Command | Description |
|--------|-------------|
| `/pm <user> <msg>` | Private message |
| `/typing` | Typing indicator |
| `/stoppedtyping` | Stop indicator |
| `/askgpt <prompt>` | OpenAI GPT request |
| `/list` | User list |
| `/announce <msg>` | Admin broadcast |
| `/kick <user>` | Kick user |
| `/changepw <user> <pw>` | Change password |
| `/rename <old> <new>` | Rename user |
| `/exit-server` | Shutdown server |

---

## 🔒 Security Notes
- Passwords always hashed  
- API key never stored in code  
- CSV sanitized  
- Safe for Excel import  

---

## 🔮 Future Extensions
- GUI Client (Swing/JavaFX)  
- File transfer  
- MySQL authentication  
- End-to-end encryption  
- Web admin dashboard  

---

## ✔ Ready for Submission
This README is formatted for **GitHub**, **professors**, and **project reports**.
