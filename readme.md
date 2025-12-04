📡 Java Socket Chat Server
Secure • Multi-User • Admin Controls • AI Integration • Full Logging • History Buffer
(Final README for the code in full code.txt — see file citation)

full code
________________________________________
🚀 Overview
This project is a fully-featured multi-client chat server built using Java Sockets. It supports:
✔ Authentication System
•	Username + password login
•	Secure hashed passwords (SHA-256)
•	Signup for new users
•	Automatic creation of a default admin account
•	Admin status persisted in users.txt
✔ Chat System
•	Global broadcast messaging
•	Private messaging /pm <user> <msg>
•	Typing indicators (start/stop)
•	Automatic message formatting
•	Real-time join/leave announcements
•	Server-side history buffer (up to 1000 messages)
✔ AI Chat Integration
•	/askgpt <prompt> sends a request to OpenAI GPT
•	Replies streamed to user
•	Logged as AI messages
•	API key read from OPENAI_API_KEY environment variable
✔ Admin Tools
•	/kick <user>
•	/changepw <user> <newpw>
•	/rename <old> <new>
•	/announce <message>
•	/exit-server
•	View IP/Port of all users
•	Server shutdown logging
✔ Logging System
Logs stored in logs/:
1.	chat_history.csv – all chat, system, AI, private messages
2.	connections.csv – login, logout, kick, admin events, server shutdown
✔ Server Console (Menu)
•	Start/stop server
•	Change port
•	Show logs paths
•	Reload users.txt
•	Show active connections
•	Exit application
________________________________________
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
________________________________________
🔧 Features Breakdown
1. 🔐 User Authentication
Authentication handled via UserManager:
Supported formats inside users.txt:
username:password
username:$sha256$<sha256hash>
username:$sha256$<sha256hash>:admin
New users
•	Created on the fly if username does not exist
•	Passwords immediately become SHA-256 hashed
Old plaintext users
•	Still supported
•	Upgraded to hashed once admin changes password
Admin auto-creation
If no admin exists, the system creates:
admin:$sha256$<hash of 'admin'>:admin
________________________________________
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
________________________________________
3. 📝 Chat History Buffer
Server keeps the last 1000 messages in memory.
Newly logged-in user receives:
=== Last 1000 Messages ===
<messages>
=== End of History ===
________________________________________
4. ⌨ Typing Indicators
Client sends:
/typing
/stoppedtyping
Server broadcasts without logging:
[SYSTEM] Bob is typing...
[SYSTEM] Bob stopped typing.
________________________________________
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
________________________________________
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
________________________________________
7. 🧾 Logging System
chat_history.csv
Columns:
timestamp,from_user,to_user,message_type,message
Logged for:
•	broadcast messages
•	private messages
•	system events
•	admin events
•	AI responses
connections.csv
Columns:
timestamp,username,ip,port,event_type
Logged for:
•	CONNECT
•	LOGIN_SUCCESS
•	LOGIN_FAIL
•	SIGNUP_SUCCESS
•	DISCONNECT
•	ADMIN_ACTION:...
•	SERVER_SHUTDOWN
________________________________________
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
________________________________________
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
________________________________________
🌐 Client Usage Guide
After connecting:
Login flow:
Username:
Password:
If username doesn’t exist:
Username not found. Do you want to sign up?
yes/no
Commands:
Command	Description
/pm <user> <msg>	Private message
/list	View users (admin sees IPs)
/typing	Show typing indicator
/stoppedtyping	Remove typing indicator
/askgpt <prompt>	Ask OpenAI
/announce <msg>	(Admin) Broadcast admin message
/kick <user>	(Admin) Disconnect user
/changepw <user> <pw>	(Admin) Change password
/rename <old> <new>	(Admin) Rename user
/exit-server	(Admin) Shutdown server
________________________________________
🔒 Security Notes
Passwords:
•	All new or changed passwords → SHA-256 hashed
•	Old plaintext entries still allowed (legacy mode)
OpenAI API:
•	HTTPS secure request
•	JSON escaping applied
•	Key never hard-coded
Logging:
•	Messages escaped for safe CSV writing
•	IP addresses logged for security audit
________________________________________
🧩 How the System Works (Architecture Summary)
Sequence for a new connection:
1.	Client connects → server logs CONNECT
2.	Login/signup handled
3.	Last 1000 messages are sent
4.	User is added to active client map
5.	Join message broadcasted
6.	User can chat, PM, ask GPT, etc.
Thread Model:
•	One thread per client (ExecutorService cached thread pool)
•	Separate accept thread
•	Separate AI request threads
Concurrency:
•	UserManager: synchronized
•	Logger: synchronized
•	History buffer: synchronized
•	Active clients stored in ConcurrentHashMap
________________________________________
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
________________________________________
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
________________________________________
📦 Future Extensions (Optional Ideas)
•	GUI Client (JavaFX/Swing)
•	File transfer between clients
•	End-to-end encryption
•	Admin dashboard (web-based)
•	Rate limiting / anti-spam
•	Database-backed user storage (MySQL, SQLite)
________________________________________



