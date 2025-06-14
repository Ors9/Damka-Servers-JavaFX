import java.io.Serializable;

public class BoardStateClients implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum CellState {
		PLAYER1, PLAYER2, EMPTY
	}

	private CellState[][] gridBoard;

	private CellState currentPlayerTurn;

	public BoardStateClients() {
		currentPlayerTurn = CellState.PLAYER1;

		gridBoard = new CellState[8][8];

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if ((row + col) % 2 == 1) {
					if (row <= 2) {
						gridBoard[row][col] = CellState.PLAYER1;
					} else if (row >= 5) {
						gridBoard[row][col] = CellState.PLAYER2;
					} else {
						gridBoard[row][col] = CellState.EMPTY;
					}
				} else {
					gridBoard[row][col] = CellState.EMPTY;
				}
			}
		}
	}

	public void eatPlayer(int i, int j) {
		gridBoard[i][j] = CellState.EMPTY;
	}

	public CellState getBoardCell(int i, int j) {
		return gridBoard[i][j];
	}

	public int isWinner() {
		int p1 = countPieces(CellState.PLAYER1);
		int p2 = countPieces(CellState.PLAYER2);

		if (p1 > 0 && p2 == 0)
			return 1;
		if (p2 > 0 && p1 == 0)
			return 2;

		return -1; // עדיין אין מנצח
	}

	public CellState getWinnerIfGameOver() {
		boolean p1HasMoves = hasAnyValidMove(CellState.PLAYER1);
		boolean p2HasMoves = hasAnyValidMove(CellState.PLAYER2);
		int p1Count = countPieces(CellState.PLAYER1);
		int p2Count = countPieces(CellState.PLAYER2);

		if ((p1Count == 0 || !p1HasMoves) && (p2Count > 0 && p2HasMoves))
			return CellState.PLAYER2;
		if ((p2Count == 0 || !p2HasMoves) && (p1Count > 0 && p1HasMoves))
			return CellState.PLAYER1;

		return CellState.EMPTY; // אין מנצח עדיין
	}

	public boolean hasAnyValidMove(CellState player) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (gridBoard[i][j] == player) {
					if (canMoveOrEat(i, j, player)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean canMoveOrEat(int row, int col, CellState player) {
		int dir = (player == CellState.PLAYER1) ? 1 : -1;
		int[] deltaCols = { -1, 1 };

		for (int dc : deltaCols) {
			int newRow = row + dir;
			int newCol = col + dc;

			// תזוזה רגילה
			if (inBounds(newRow, newCol) && gridBoard[newRow][newCol] == CellState.EMPTY) {
				return true;
			}

			// אכילה
			int jumpRow = row + 2 * dir;
			int jumpCol = col + 2 * dc;
			int midRow = row + dir;
			int midCol = col + dc;

			if (inBounds(jumpRow, jumpCol) && gridBoard[jumpRow][jumpCol] == CellState.EMPTY
					&& gridBoard[midRow][midCol] != CellState.EMPTY && gridBoard[midRow][midCol] != player) {
				return true;
			}
		}

		return false;
	}

	private int countPieces(CellState player) {
		int count = 0;
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (gridBoard[row][col] == player)
					count++;
			}
		}
		return count;
	}

	public void switchTurn() {
		if (currentPlayerTurn == CellState.PLAYER1) {
			currentPlayerTurn = CellState.PLAYER2;
		} else {
			currentPlayerTurn = CellState.PLAYER1;
		}
	}

	public CellState whoTurn() {
		return currentPlayerTurn;
	}

	public void printBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				switch (gridBoard[i][j]) {
				case PLAYER1:
					System.out.print("1 ");
					break;
				case PLAYER2:
					System.out.print("2 ");
					break;
				default:
					System.out.print(". ");
					break;
				}
			}
			System.out.println();
		}
	}

	private boolean inBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	public boolean TryMoveOrEat(int[] move) {
		int fromRow = move[0];
		int fromCol = move[1];
		int toRow = move[2];
		int toCol = move[3];

		if (!inBounds(fromRow, fromCol) || !inBounds(toRow, toCol))
			return false;

		CellState fromCell = gridBoard[fromRow][fromCol];
		CellState toCell = gridBoard[toRow][toCol];

		// חייב להיות תור של השחקן הזה
		if (fromCell != currentPlayerTurn)
			return false;

		// התא שאליו זזים חייב להיות ריק
		if (toCell != CellState.EMPTY)
			return false;

		int rowDiff = toRow - fromRow;
		int colDiff = Math.abs(toCol - fromCol);

		if (currentPlayerTurn == CellState.PLAYER1 && rowDiff == 1 && colDiff == 1
				|| currentPlayerTurn == CellState.PLAYER2 && rowDiff == -1 && colDiff == 1) {

			// ביצוע המהלך
			gridBoard[toRow][toCol] = fromCell;
			gridBoard[fromRow][fromCol] = CellState.EMPTY;
			return true;
		}

		if ((currentPlayerTurn == CellState.PLAYER1 && rowDiff == 2 && Math.abs(toCol - fromCol) == 2)
				|| (currentPlayerTurn == CellState.PLAYER2 && rowDiff == -2 && Math.abs(toCol - fromCol) == 2)) {

			int midRow = fromRow + rowDiff / 2;
			int midCol = fromCol + (toCol - fromCol) / 2;
			CellState midCell = gridBoard[midRow][midCol];

			// האם באמצע יש יריב?
			if ((currentPlayerTurn == CellState.PLAYER1 && midCell == CellState.PLAYER2)
					|| (currentPlayerTurn == CellState.PLAYER2 && midCell == CellState.PLAYER1)) {

				// בצע אכילה
				gridBoard[toRow][toCol] = fromCell;
				gridBoard[fromRow][fromCol] = CellState.EMPTY;
				gridBoard[midRow][midCol] = CellState.EMPTY; // הסר את היריב
				return true;
			}
		}

		return false;

	}

}
