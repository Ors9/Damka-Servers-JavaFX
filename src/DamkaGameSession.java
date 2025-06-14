import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class DamkaGameSession extends Thread {
	private Socket p1, p2;
	private BoardStateClients boardGame;

	public DamkaGameSession(Socket p1, Socket p2) {
		this.p1 = p1;
		this.p2 = p2;
		boardGame = new BoardStateClients();
	}

	@Override
	public void run() {
		try {
			// כאן תנהל את המשחק – שלח/קבל מהשחקנים
			System.out.println("🕹️ Game thread started between two players");

			// דוגמה: שליחת הודעת פתיחה לכל שחקן
			ObjectOutputStream out1 = new ObjectOutputStream(p1.getOutputStream());
			ObjectInputStream in1 = new ObjectInputStream(p1.getInputStream());

			ObjectOutputStream out2 = new ObjectOutputStream(p2.getOutputStream());
			ObjectInputStream in2 = new ObjectInputStream(p2.getInputStream());

			out1.writeObject(BoardStateClients.CellState.PLAYER1);
			out2.writeObject(BoardStateClients.CellState.PLAYER2);

			out1.writeObject(boardGame);
			out2.writeObject(boardGame);

			DamkaGameLogic(out1, out2, in1, in2);

			// המשך לוגיקת המשחק...
		} catch (IOException e) {
			System.out.println("❌ Game session error: " + e.getMessage());
		} finally {
			try {
				if (!p1.isClosed())
					p1.close();
				if (!p2.isClosed())
					p2.close();
				System.out.println("🔚 Game session ended.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void DamkaGameLogic(ObjectOutputStream out1, ObjectOutputStream out2, ObjectInputStream in1,
			ObjectInputStream in2) {
		try {
			while (true) {
				BoardStateClients.CellState winner = boardGame.getWinnerIfGameOver();
				if (winner != BoardStateClients.CellState.EMPTY) {
					out1.writeObject(winner);
					out2.writeObject(winner);
					break;
				}

				ObjectInputStream currIn = (boardGame.whoTurn() == BoardStateClients.CellState.PLAYER1) ? in1 : in2;
				ObjectOutputStream currOut = (boardGame.whoTurn() == BoardStateClients.CellState.PLAYER1) ? out1 : out2;
				ObjectOutputStream opponentOut = (boardGame.whoTurn() == BoardStateClients.CellState.PLAYER1) ? out2
						: out1;


				Object obj = currIn.readObject();
				int[] move = null;

				if (obj instanceof int[]) {
					move = (int[]) obj;
				}

				boolean validMove = boardGame.TryMoveOrEat(move);

				if (validMove) {
					boardGame.switchTurn();
				}
				

				boardGame.printBoard();

				currOut.reset();
				currOut.writeObject(boardGame);

				opponentOut.reset();
				opponentOut.writeObject(boardGame);

			}

		} catch (Exception e) {
			System.out.println("❌ Error in game logic: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
