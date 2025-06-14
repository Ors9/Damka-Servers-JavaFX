# Damka-Servers-JavaFX
Damka with server and clients with javaFX

🧩 Technical Structure
This is a two-player Checkers (Damka) game implemented in Java using JavaFX and TCP/IP Sockets.

🗂️ Project Overview
Language: Java 17+

Framework: JavaFX (for GUI)

Architecture: Client-Server using TCP sockets (ObjectInputStream / ObjectOutputStream)

📁 Main Components
File/Class	Description
DamkaServer.java	Handles incoming client connections, manages the game session, turn logic, and board updates.
DamkaClient.java	Launches the JavaFX GUI client for the player. Connects to the server and sends moves.
BoardStateClients.java	Represents the board, handles game rules (valid moves, captures, win conditions), and is shared between server and clients.
DamkaGameController.java	The JavaFX controller for the client GUI: handles clicks, highlights, and UI updates.

🖥️ Manual Run Instructions
This project does not require an installer or .exe file. You simply run each component manually using your IDE or terminal.

✅ Step 1: Start the Server
Open DamkaServer.java

Run it
You should see:
✅ Server started successfully. Waiting for players...

🎮 Step 2: Start Two Clients
Open DamkaClient.java

Run it twice – each instance represents one player

The first client will connect as Player 1

The second client will connect as Player 2

Make sure to start the server first, and then run the clients one by one.

🔁 Local Play (Same Machine)
All components run on localhost, so you can play locally on one machine without changing any IP or network settings.

If you'd like, I can add an "Advanced Setup" section later for connecting two clients over LAN or preparing a .bat or .exe file. Let me know!

---

## 🖼️ Screenshots

| State         | Description                                       |
|---------------|---------------------------------------------------|
| ![Start](![image](https://github.com/user-attachments/assets/60254944-2779-4069-8525-52d0e68685d9)
)   | Game start – both players connected, board initialized. |
| ![Step](![image](https://github.com/user-attachments/assets/fd4191ec-e539-4a7c-b5d7-104137a7865f)
)     | A regular turn – a piece was selected and moved.         |
| ![Capture](![image](https://github.com/user-attachments/assets/013d124d-98b4-43ab-bd15-368692fdf624)
) | Capture move – a piece jumps over and eats opponent.     |
| ![End](![image](https://github.com/user-attachments/assets/2c63c379-5d0e-43c9-b6fa-7a78e5a61af6)
)       | Game over – a winner is declared (or no more moves).     |




