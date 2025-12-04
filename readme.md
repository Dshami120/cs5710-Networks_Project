📡 Java Socket Chat Server
Secure • Multi-User • Admin Controls • AI Integration • Full Logging • History Buffer
(Final README for the code in full code.txt — see file citation)
full code

🚀 Overview
This project is a fully-featured multi-client chat server built using Java Sockets. It supports:
✔ Authentication System


Username + password login


Secure hashed passwords (SHA-256)


Signup for new users


Automatic creation of a default admin account


Admin status persisted in users.txt


✔ Chat System


Global broadcast messaging


Private messaging /pm <user> <msg>


Typing indicators (start/stop)


Automatic message formatting


Real-time join/leave announcements


Server-side history buffer (up to 1000 messages)


✔ AI Chat Integration


/askgpt <prompt> sends a request to OpenAI GPT


Replies streamed to user


Logged as AI messages


API key read from OPENAI_API_KEY environment variable


✔ Admin Tools


/kick <user>


/changepw <user> <newpw>


/rename <old> <new>


/announce <message>


/exit-server


View IP/Port of all users


Server shutdown logging


✔ Logging System
Logs stored in logs/:


chat_history.csv – all chat, system, AI, private messages


connections.csv – login, logout, kick, admin events, server shutdown


✔ Server Console (Menu)


Start/stop server


Change port


Show logs paths


Reload users.txt


Show active connections


Exit application



📁 Project Structure
/ChatServerProject
│
├── ServerMain.java        # Server entry point with interactive menu
├── ChatServer.java        # Core server functionality
├── ClientHandler.java     # One instance per connected client
├── ChatClient.java        # Simple terminal-based client
├── User.java              # User model
├── UserManager.java       # Handles users.txt + password hashing
├── ChatLogger.java        # Chat & connection logging
├── ServerUtils.java       # Timestamp + CSV utilities
├── MessageType.java       # (Enum – implied) Message categories
├── AIClient.java          # OpenAI GPT API wrapper
│
├── users.txt              # Accounts database
└── logs/
    ├── chat_history.csv
    └── connections.csv


🔧 Features Breakdown
1. 🔐 User Authentication
Authentication handled via UserManager:
Supported formats inside users.txt:
username:password
username:$sha256$<sha256hash>
username:$sha256$<sha256hash>:admin

New users


Created on the fly if username does not exist


Passwords immediately become SHA-256 hashed


Old plaintext users


Still supported


Upgraded to hashed once admin changes password


Admin auto-creation
If no admin exists, the system creates:
admin:$sha256$<hash of 'admin'>:admin


2. 💬 Messaging System
Broadcast messages
Everyone receives:
username: message

Private Message Command
/pm bob hello there!

Sender sees:
[PM to bob] hello there!

Receiver sees:
[PM from alice] hello there!

All PMs are logged correctly.

3. 📝 Chat History Buffer
Server keeps the last 1000 messages in memory.
Newly logged-in user receives:
=== Last 1000 Messages ===
<messages>
=== End of History ===


4. ⌨ Typing Indicators
Client sends:
/typing
/stoppedtyping

Server broadcasts without logging:
[SYSTEM] Bob is typing...
[SYSTEM] Bob stopped typing.


5. 🤖 AI Integration (/askgpt)
Example:
/askgpt Write a poem about Java sockets.

Server spawns a background thread, calls the OpenAI API, returns the response.
AI messages logged under MessageType.AI.
Requirements
Set your key:
Windows (PowerShell):
setx OPENAI_API_KEY "your_key_here"

Mac/Linux:
export OPENAI_API_KEY="your_key_here"


6. 🛡 Admin Commands
/announce <msg>
Broadcast an admin message.
/kick <user>
Disconnects user immediately.
/changepw <user> <newpw>
Changes stored password (hashed automatically).
/rename <old> <new>
Renames both user account and live connection.
/exit-server
Gracefully shuts down server.

7. 🧾 Logging System
chat_history.csv
Columns:
timestamp,from_user,to_user,message_type,message

Logged for:


broadcast messages


private messages


system events


admin events


AI responses


connections.csv
Columns:
timestamp,username,ip,port,event_type

Logged for:


CONNECT


LOGIN_SUCCESS


LOGIN_FAIL


SIGNUP_SUCCESS


DISCONNECT


ADMIN_ACTION:...


SERVER_SHUTDOWN



8. 🖥 Server Console (ServerMain)
Run:
java ServerMain

You get:
1. Start server
2. Stop server
3. Set listening port
4. View active connections
5. Show path to chat_history.csv
6. Show path to connections.csv
7. Reload users.txt
8. Exit application

This makes the server extremely easy to operate.

🛠 How to Run
1. Compile
javac *.java

2. Start server
java ServerMain

3. Start client (in a separate terminal)
java ChatClient

If server is local and default port:
Host: localhost
Port: 12345


🌐 Client Usage Guide
After connecting:
Login flow:
Username:
Password:

If username doesn’t exist:
Username not found. Do you want to sign up?
yes/no

Commands:
CommandDescription/pm <user> <msg>Private message/listView users (admin sees IPs)/typingShow typing indicator/stoppedtypingRemove typing indicator/askgpt <prompt>Ask OpenAI/announce <msg>(Admin) Broadcast admin message/kick <user>(Admin) Disconnect user/changepw <user> <pw>(Admin) Change password/rename <old> <new>(Admin) Rename user/exit-server(Admin) Shutdown server

🔒 Security Notes
Passwords:


All new or changed passwords → SHA-256 hashed


Old plaintext entries still allowed (legacy mode)


OpenAI API:


HTTPS secure request


JSON escaping applied


Key never hard-coded


Logging:


Messages escaped for safe CSV writing


IP addresses logged for security audit



🧩 How the System Works (Architecture Summary)
Sequence for a new connection:


Client connects → server logs CONNECT


Login/signup handled


Last 1000 messages are sent


User is added to active client map


Join message broadcasted


User can chat, PM, ask GPT, etc.


Thread Model:


One thread per client (ExecutorService cached thread pool)


Separate accept thread


Separate AI request threads


Concurrency:


UserManager: synchronized


Logger: synchronized


History buffer: synchronized


Active clients stored in ConcurrentHashMap



🗄 Required Files
Before starting server, ensure:
users.txt exists
If missing, system creates it and adds default admin.
Example:
admin:$sha256$9ef...:admin
bob:$sha256$ab3...
alice:$sha256$8dd...

logs/ folder
Created automatically on first run.

🧪 Example Session
User connects:
Welcome to the Java Chat Server.
Please log in.
Username: bob
Password: ****
[SYSTEM] Login successful. Welcome, bob.

Sends message:
bob: Hello everyone!

Admin kicks a user:
/kick bob
[SYSTEM] bob was kicked by admin alice.

AI usage:
/askgpt tell me a joke
[AI] Why do Java developers wear glasses? Because they don't C#.


📦 Future Extensions (Optional Ideas)


GUI Client (JavaFX/Swing)


File transfer between clients


End-to-end encryption


Admin dashboard (web-based)


Rate limiting / anti-spam


Database-backed user storage (MySQL, SQLite)



🏁 Conclusion
This README fully documents everything included in the final version of your Secure Multi-Client Java Chat Server, covering:


Authentication


Admin features


Logging


Message history


AI integration


Server console tools


All commands and security behavior


📡 Java Socket Chat Server
Secure • Multi-User • Admin Controls • AI Integration • Full Logging • History Buffer

(Final README for the code in full code.txt — now with full requirements sheet)

📋 1. Requirements Specification Sheet

This section defines all functional and non-functional requirements for the system based on the final implementation.

✅ 1.1 Functional Requirements
User Accounts & Authentication

The system shall allow users to log in using a username and password.

The system shall store new and updated passwords using SHA-256 hashing.

The system shall allow new users to sign up if their username does not exist.

The system shall automatically create a default administrator account (admin/admin) if no admin exists.

The system shall support detecting and refusing login after 3 failed password attempts.

Messaging & Communication

The system shall allow users to send broadcast messages to all connected users.

The system shall allow private direct messages using /pm <user> <message>.

The system shall broadcast join and leave events to all users.

The system shall maintain a 1000-message history buffer and send it to newly logged-in users.

The system shall provide typing indicators using /typing and /stoppedtyping.

Admin Tools

The system shall allow admins to kick users using /kick <user>.

The system shall allow admins to rename users using /rename <old> <new>.

The system shall allow admins to change user passwords using /changepw <user> <pw>.

The system shall allow admins to make announcements using /announce <msg>.

The system shall allow admins to shut down the server using /exit-server.

AI Integration

The system shall allow users to query OpenAI using /askgpt <prompt>.

The system shall require the environment variable OPENAI_API_KEY to be set.

Server Console (Menu)

The system shall provide an interactive console menu to:

Start/stop the server

Set the listening port

Show active connections (username + IP + port)

Show paths to logs

Reload user accounts

Logging

The system shall log all chat, private, system, admin, and AI messages to logs/chat_history.csv.

The system shall log all connects, disconnects, and admin actions to logs/connections.csv.

The system shall log server shutdown events.

⚙️ 1.2 Non-Functional Requirements
Performance

The system shall support multiple concurrent clients using thread-per-connection architecture.

The system shall respond to messages in real-time with minimal latency.

Reliability

The server shall handle unexpected client disconnections gracefully.

The server shall create required directories (logs/) and files (chat_history.csv, connections.csv) if missing.

Security

Passwords shall be securely hashed using SHA-256.

The API key shall not be hard-coded and must come from system environment variables.

Usability

The client shall show all server messages clearly in terminal.

The console menu shall allow operators to manage server operations without programming knowledge.

Compatibility

The system shall run on any OS supporting Java 17+ (Windows, MacOS, Linux).

🧭 2. System Capabilities (What the System Does)

This section summarizes what the final application is designed to accomplish.

✔ Supports multi-client chat over TCP sockets

Real-time text communication via broadcast or private messages.

✔ Secure login & signup with hashed passwords

SHA-256 hashing used for all new or updated accounts.

✔ Admin control panel via commands

Kick, rename, change password, announcements, shutdown.

✔ AI-powered chatbot integration

Users can ask questions with /askgpt.

✔ Typing indicators

Live typing status shown to all clients.

✔ Recent history replay

New users receive the last 1000 messages on login.

✔ Detailed logging

Every action stored in CSV logs for auditing or analysis.

✔ Restartable server

Can be started/stopped safely from the console menu.

✔ Graceful handling of disconnects

Users who disconnect unexpectedly are removed cleanly.

✔ Force-renaming users

Admins may rename accounts while user is active.

❌ 3. System Limitations (What the System Does NOT Do)

Important to clarify (especially for teachers, graders, or GitHub readers):

❌ No encrypted sockets (TLS)

All communication is plaintext TCP.
(Encryption could be added via SSLServerSocket.)

❌ No graphical user interface included

Only a CLI client is provided (terminal-based).
(GUI could be added with JavaFX or Swing.)

❌ No file transfer support

The system does not send files between users.

❌ No anti-spam or rate limiting

Users may send unlimited messages.

❌ No persistent message storage

Chat history buffer is in-memory; server restarts erase it.

❌ No multi-room (channels) support

All users occupy a single global chat room.

❌ No automated banning system

Admins can kick users but cannot ban permanently.

❌ No email verification or password reset

Signup is instant and local-only.

❌ No GUI for server administration

The server uses a terminal menu only.