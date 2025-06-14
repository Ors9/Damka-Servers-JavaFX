import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class DamkaServer {
	public static int PORT = 8888;

	public static void main(String[] args) {
		ServerSocket srvSct = null;
		try {
			System.out.println("🔵 Starting Damka server on port " + PORT + "...");
			srvSct = new ServerSocket(PORT);
			System.out.println("✅ Server started successfully. Waiting for players...");

			while (true) {
				System.out.println("⏳ Waiting for Player 1 to connect...");
				Socket p1 = srvSct.accept();
				System.out.println("🟢 Player 1 connected from " + p1.getInetAddress().getHostAddress());

				System.out.println("⏳ Waiting for Player 2 to connect...");
				Socket p2 = srvSct.accept();
				System.out.println("🟢 Player 2 connected from " + p2.getInetAddress().getHostAddress());

				System.out.println("🎮 Starting new Damka game session for two players.");
				DamkaGameSession gameSession = new DamkaGameSession(p1, p2);
				gameSession.start();
			}

		} catch (IOException e) {
			System.out.println("❌ Server error: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "❌ Server error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (srvSct != null && !srvSct.isClosed()) {
					srvSct.close();
					System.out.println("🔴 Server closed.");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
