📡 Java Socket Chat Server
Secure • Multi-User • Admin Controls • AI Integration • Logging • History Buffer
🚀 Overview

This project is a fully-featured multi-client chat server written in pure Java Sockets, built for advanced networking coursework and real-world use.

It includes:

🔐 Authentication System

Username + password login

SHA-256 hashed passwords

Automatic upgrade of legacy plaintext passwords

Signup for new users

Automatic creation of a default admin account

Admin privileges tracked inside users.txt

💬 Chat System

Global broadcast messaging

Private messaging using /pm <user> <msg>

Typing indicators (start/stop)

Join/leave announcements

1000-message server-side history buffer

Auto-formatting and full logging

🤖 AI Chat Integration (OpenAI GPT)

/askgpt <prompt> executes GPT request

Responses streamed to the client

Logged as AI messages

API key loaded from OPENAI_API_KEY environment variable

🛡 Admin Tools
/kick <user>
/changepw <user> <newpw>
/rename <old> <new>
/announce <msg>
/exit-server
/list (admin sees IP + port)

🧾 Server Logging System

Stored inside /logs:

chat_history.csv — all chats, PMs, AI messages, system events

connections.csv — connects, disconnects, kicks, login failures, shutdowns

🖥 Server Console Menu

Run via:

java ServerMain


From here, you can:

Start/stop server

Change port

View active connections

View chat and connection logs

Reload users.txt

Gracefully shut down the server

📁 Project Structure
/ChatServerProject
│
├── ServerMain.java         # Server entry point with interactive console menu
├── ChatServer.java         # Core server class (socket listener, broadcast)
├── ClientHandler.java      # Handles each connected client
├── ChatClient.java         # Terminal-based client program
│
├── User.java               # User model
├── UserManager.java        # Handles users.txt, hashing, admin roles
│
├── ChatLogger.java         # CSV logging for chat + connections
├── ServerUtils.java        # Timestamp + clean CSV escape functions
├── MessageType.java        # Enum representing message categories
├── AIClient.java           # OpenAI GPT API client
│
├── users.txt               # User credential database
└── logs/
    ├── chat_history.csv
    └── connections.csv

🔧 Feature Breakdown (Detailed)
1️⃣ 🔐 User Authentication
Users file formats supported
username:password
username:$sha256$<hash>
username:$sha256$<hash>:admin

Signup Workflow

If username does not exist:

User is prompted to create an account

Password is hashed instantly

User entry written to users.txt

Auto-Admin

If no admin exists, the server creates:

admin:$sha256$<hash-of-admin>:admin

2️⃣ 💬 Messaging System
Broadcast
bob: Hello everyone!

Private Messaging

Command:

/pm alice hey what's up?


Sender sees:

[PM to alice] hey what's up?


Receiver sees:

[PM from bob] hey what's up?


✔ All private messages are logged (with hidden content)
✔ No one else sees PMs

3️⃣ 🧠 Chat History Buffer

New client receives:

=== Last 1000 Messages ===
<...history...>
=== End of History ===

4️⃣ ⌨️ Typing Indicators

Client sends:

/typing
/stoppedtyping


Broadcasts:

[SYSTEM] Bob is typing...
[SYSTEM] Bob stopped typing.


No logs saved for typing events.

5️⃣ 🤖 AI Integration using /askgpt

Example:

/askgpt Write a poem about Java sockets.


Server:

Spawns async thread

Calls OpenAI GPT model

Streams lines back to requester

Logs message under MessageType.AI

Environment Setup

Windows:

setx OPENAI_API_KEY "yourkey"


Linux/Mac:

export OPENAI_API_KEY="yourkey"

6️⃣ 🛡 Admin Commands
Command	Description
/announce <msg>	Global admin announcement
/kick <user>	Immediately disconnect user
/changepw <user> <pw>	Change user's password
/rename <old> <new>	Rename user live + in file
/exit-server	Gracefully shuts down
/list	Admin sees usernames + IP:port

Admins are defined via :admin tag in users.txt.

7️⃣ 🧾 Logging System
chat_history.csv

Columns:

timestamp,from_user,to_user,message_type,message


Logs:

Broadcast

PMs

System events

Admin events

AI responses

connections.csv

Columns:

timestamp,username,ip,port,event_type


Logs:

CONNECT

LOGIN_SUCCESS

LOGIN_FAIL

SIGNUP_SUCCESS

DISCONNECT

ADMIN_ACTION

SERVER_SHUTDOWN

8️⃣ 🖥 Server Console (ServerMain)

When running:

java ServerMain


Menu:

1. Start server
2. Stop server
3. Change port
4. View active connections
5. Show chat_history.csv path
6. Show connections.csv path
7. Reload users.txt
8. Exit application

🛠 How to Compile & Run
1. Compile
javac *.java

2. Start server
java ServerMain

3. Start client
java ChatClient


Default host/port:

Host: localhost
Port: 12345

🌐 Client Usage Guide
Commands Available
Command	Description
/pm <user> <msg>	Send private message
/list	List users (admin sees IP/port)
/typing	Send typing indicator
/stoppedtyping	Stop indicator
/askgpt <prompt>	Ask OpenAI
/announce <msg>	Admin broadcast
/kick <user>	Admin kick
/changepw <user> <pw>	Admin changepw
/rename <old> <new>	Rename user
/exit-server	Shutdown server
🔒 Security Notes
Passwords

New and updated passwords → SHA-256 hashed

Legacy plaintext users allowed but upgraded when password changes

OpenAI API

Key never stored in code

HTTPS encryption

All content sanitized

CSV Logging

All fields escaped

Safe for Excel import

🧩 System Architecture Summary
Connection Flow

Client connects → server logs CONNECT

Authentication / signup

Server sends last 1000 message history

User added to active client map

Join message broadcast

User may chat, PM, ask GPT, or use admin commands

Threading Model

One thread per client (cached thread pool)

Separate accept thread

Separate AI threads

Concurrency

UserManager synchronized

Logger synchronized

History buffer synchronized

Active users: ConcurrentHashMap

🧪 Example Session
Welcome to the Java Chat Server.
Username: bob
Password: ****
[SYSTEM] Login successful. Welcome, bob!

bob: Hello everyone!


Admin:

/kick bob


Server:

[SYSTEM] bob was kicked by admin alice.


AI:

/askgpt tell me a joke
[AI] Why do Java developers wear glasses? Because they don't C#.

📦 Future Extensions

GUI Client (JavaFX/Swing)

File transfer between clients

End-to-end encryption

MySQL-based authentication

Web dashboard for admin

Anti-spam filters

✅
