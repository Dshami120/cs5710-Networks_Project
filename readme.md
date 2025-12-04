📡 Java Socket Chat Server
Secure • Multi-User • Admin Controls • AI Integration • Logging • History Buffer
A fully-featured multi-client chat server built in pure Java Sockets, designed for advanced networking coursework and real-world applications.
This server includes authentication, admin tools, chat history, AI assistance, logging, and robust concurrency handling.
________________________________________
🌟 Features at a Glance
🔐 Authentication System
•	Username + password login
•	SHA-256 hashed passwords
•	Automatic upgrade of legacy plaintext passwords
•	Signup for new users
•	Automatic creation of default admin
•	Admin privileges stored in users.txt
________________________________________
💬 Chat System
•	Global broadcast chat
•	Private messaging /pm <user> <msg>
•	Typing indicators
•	Join/leave announcements
•	1000-message history buffer sent to new users
•	Auto-formatted messages
•	Full CSV logging
________________________________________
🤖 AI Chat Integration (OpenAI GPT)
•	/askgpt <prompt> generates GPT responses
•	Responses streamed to client
•	Logged as AI messages
•	API key read from environment variable:
o	Windows: setx OPENAI_API_KEY "yourkey"
o	Linux/Mac: export OPENAI_API_KEY="yourkey"
________________________________________
🛡 Admin Tools
Command	Description
/kick <user>	Disconnect a user immediately
/changepw <user> <pw>	Force-change password
/rename <old> <new>	Rename a user live + in file
/announce <msg>	Server-wide announcement
/list	Show connected users (admin sees IP + port)
/exit-server	Gracefully shut down the server
________________________________________
🧾 Logging System
chat_history.csv
•	timestamp
•	from_user
•	to_user
•	message_type
•	message
Logs:
•	Broadcasts
•	Private messages
•	System events
•	Admin actions
•	AI responses
connections.csv
•	timestamp
•	username
•	ip
•	port
•	event_type
Logs:
•	CONNECT
•	LOGIN_SUCCESS
•	LOGIN_FAIL
•	SIGNUP_SUCCESS
•	DISCONNECT
•	ADMIN_ACTION
•	SERVER_SHUTDOWN
________________________________________
🖥 Server Console Menu
Run using:
java ServerMain
Menu options:
1.	Start server
2.	Stop server
3.	Change port
4.	View active connections
5.	Show chat_history.csv path
6.	Show connections.csv path
7.	Reload users.txt
8.	Exit application
________________________________________
📁 Project Structure
ChatServerProject/
│
├── ServerMain.java         # Interactive console menu
├── ChatServer.java         # Main server listener + broadcast manager
├── ClientHandler.java      # Threaded per-client handler
├── ChatClient.java         # Terminal-based client program
│
├── User.java               # User model
├── UserManager.java        # Handles credentials, hashing, and users.txt
│
├── ChatLogger.java         # CSV logger for chat + connection events
├── ServerUtils.java        # Timestamp + safe CSV escaping
├── MessageType.java        # Enum for message categories
├── AIClient.java           # OpenAI GPT client
│
├── users.txt               # User credential database
└── logs/
    ├── chat_history.csv
    └── connections.csv
________________________________________
🔧 Setup & Installation
1️⃣ Compile the project
javac *.java
2️⃣ Start the server
java ServerMain
3️⃣ Start a client
java ChatClient
Default settings:
Host: localhost
Port: 12345
________________________________________
🌐 Client Commands Reference
Command	Description
/pm <user> <msg>	Private message
/typing	Send typing indicator
/stoppedtyping	Stop typing indicator
/askgpt <prompt>	Ask OpenAI GPT
/list	Show users (admin sees IP:port)
/announce <msg>	Admin broadcast
/kick <user>	Remove user
/changepw <user> <pw>	Change user password
/rename <old> <new>	Rename user
/exit-server	Shutdown server
________________________________________
🔒 Security Notes
Passwords
•	Always hashed using SHA-256
•	Legacy plaintext automatically upgraded
•	No passwords stored in code or logs
OpenAI Integration
•	API key only loaded from environment variables
•	Never written to disk
Logging Safety
•	All fields escaped for safe CSV import
•	AI responses logged without exposing API key
________________________________________
🧩 System Architecture Summary
Connection Flow
1.	Client connects → logged
2.	Authentication or signup
3.	Server sends last 1000 messages
4.	User added to active map
5.	Join broadcast
6.	User can chat, PM, ask GPT, use admin tools
Thread Model
•	One thread per client
•	Server socket listener thread
•	Asynchronous GPT request threads
•	ConcurrentHashMap for active users
•	Synchronized: UserManager, Logger, History Buffer
________________________________________
🧪 Example Session
Welcome to the Java Chat Server.
Username: bob
Password: ****
[SYSTEM] Login successful. Welcome, bob!

bob: Hello everyone!
Admin kicks Bob:
/kick bob
[SYSTEM] bob was kicked by admin alice.
AI example:
/askgpt tell me a joke
[AI] Why do Java developers wear glasses? Because they don't C#.
________________________________________
🔮 Future Extensions
•	GUI Client (JavaFX/Swing)
•	File transfer between clients
•	MySQL-backed authentication
•	Full end-to-end encryption
•	Browser-based admin dashboard
•	Anti-spam / rate limiting

