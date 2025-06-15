import java.io.Serializable;

public class BoardStateClients implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum CellState {
		PLAYER1, PLAYER2, KING1 , KING2 , EMPTY
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
				case KING1:
					System.out.println("10");
					break;
				case KING2:
					System.out.println("20");
					break;
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

		boolean isPlayer1Piece = (fromCell == CellState.PLAYER1 || fromCell == CellState.KING1);
		boolean isPlayer2Piece = (fromCell == CellState.PLAYER2 || fromCell == CellState.KING2);

		if ((currentPlayerTurn == CellState.PLAYER1 && !isPlayer1Piece) ||
		    (currentPlayerTurn == CellState.PLAYER2 && !isPlayer2Piece)) {
		    return false;
		}

		// התא שאליו זזים חייב להיות ריק
		if (toCell != CellState.EMPTY)
			return false;

		if(gridBoard[fromRow][fromCol] == CellState.KING1 || gridBoard[fromRow][fromCol] == CellState.KING2 ) {
			return kingMove(fromRow , fromCol , toRow , toCol);
		}
		
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
			if ((currentPlayerTurn == CellState.PLAYER1 && (midCell == CellState.PLAYER2 || midCell == CellState.KING2))
					|| (currentPlayerTurn == CellState.PLAYER2 && (midCell == CellState.PLAYER1 || midCell == CellState.KING1))) {

				// בצע אכילה
				gridBoard[toRow][toCol] = fromCell;
				gridBoard[fromRow][fromCol] = CellState.EMPTY;
				gridBoard[midRow][midCol] = CellState.EMPTY; // הסר את היריב
				return true;
			}
		}

		return false;

	}
	
	private boolean kingMove(int fromRow , int fromCol , int toRow , int toCol) {
		int diffRow = Math.abs(toRow-fromRow);
		int diffCol = Math.abs(toCol-fromCol);
		CellState fromCell = gridBoard[fromRow][fromCol];
		CellState toCell = gridBoard[toRow][toCol];
		
	    if (toCell != CellState.EMPTY)
	        return false;
		
		if(diffRow == 1 && diffCol == 1) {
			// ביצוע המהלך
			gridBoard[toRow][toCol] = fromCell;
			gridBoard[fromRow][fromCol] = CellState.EMPTY;
			return true;
		}
		
		if(diffRow == 2 && diffCol == 2) {
			int midRow = fromRow + (toRow - fromRow) / 2;
			int midCol = fromCol + (toCol - fromCol) / 2;
			CellState midCell = gridBoard[midRow][midCol];
			
			// האם באמצע יש יריב?
			if ((currentPlayerTurn == CellState.PLAYER1 && (midCell == CellState.PLAYER2 || midCell == CellState.KING2))
					|| (currentPlayerTurn == CellState.PLAYER2 && (midCell == CellState.PLAYER1 || midCell == CellState.KING1))) {

				// בצע אכילה
				gridBoard[toRow][toCol] = fromCell;
				gridBoard[fromRow][fromCol] = CellState.EMPTY;
				gridBoard[midRow][midCol] = CellState.EMPTY; // הסר את היריב
				return true;
			}
		}
		
		
		
		return false;
	}

	public void checkIfBecomeKingAndMakeItKing() {
	    for (int i = 0; i < 8; i++) {
	        if (gridBoard[0][i] == CellState.PLAYER2)
	            gridBoard[0][i] = CellState.KING2;

	        if (gridBoard[7][i] == CellState.PLAYER1)
	            gridBoard[7][i] = CellState.KING1;
	    }
	}
	
	public boolean hasAnotherEat(int row, int col) {
	    CellState piece = gridBoard[row][col];

	    if (piece != CellState.KING1 && piece != CellState.KING2)
	        return false; // הפונקציה נועדה רק למלכים

	    int[] dr = {-2, -2, 2, 2}; // שינויים בשורה (קפיצה של 2)
	    int[] dc = {-2, 2, -2, 2}; // שינויים בעמודה

	    for (int i = 0; i < 4; i++) {
	        int newRow = row + dr[i];
	        int newCol = col + dc[i];
	        int midRow = row + dr[i] / 2;
	        int midCol = col + dc[i] / 2;

	        if (!inBounds(newRow, newCol) || !inBounds(midRow, midCol))
	            continue;

	        if (gridBoard[newRow][newCol] != CellState.EMPTY)
	            continue;

	        CellState midCell = gridBoard[midRow][midCol];

	        if (piece == CellState.KING1 &&
	            (midCell == CellState.PLAYER2 || midCell == CellState.KING2)) {
	            return true;
	        }

	        if (piece == CellState.KING2 &&
	            (midCell == CellState.PLAYER1 || midCell == CellState.KING1)) {
	            return true;
	        }
	    }

	    return false;
	}

}
