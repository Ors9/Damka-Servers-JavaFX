
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class DamkaGameController {

	@FXML
	private GridPane grid;

	@FXML
	private Label lblPlayer;

	@FXML
	private Label lblTurn;

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	private ImageView[][] soliderImgs;

	private BoardStateClients boardGame;
	private BoardStateClients.CellState player;
	private int[] selectedCell = null;
	private ImageView selectedImage = null;

	public void initialize() {
		lblPlayer.setStyle("-fx-font-size: 22px;" + "-fx-font-weight: bold;"
				+ "-fx-text-fill: linear-gradient(to right, #2196F3, #21CBF3);"
				+ "-fx-background-color: rgba(255, 255, 255, 0.2);" + "-fx-padding: 5px 10px;"
				+ "-fx-background-radius: 10px;");

		lblTurn.setStyle("-fx-font-size: 18px;" + "-fx-font-style: italic;" + "-fx-text-fill: #4CAF50;"
				+ "-fx-background-color: rgba(255,255,255,0.3);" + "-fx-padding: 4px 8px;"
				+ "-fx-background-radius: 10px;");

		new Thread(() -> {
			connectToServer(); // תהליך שעלול להיתקע, רץ ברקע
		}).start();
	}

	private void updatePlayerLabel() {
		if (player == BoardStateClients.CellState.PLAYER1) {
			lblPlayer.setText("Player 1");
		} else {
			lblPlayer.setText("Player 2");
		}

		lblPlayer.setStyle("-fx-font-size: 22px;" + "-fx-font-weight: bold;"
				+ "-fx-text-fill: linear-gradient(to right, #2196F3, #21CBF3);"
				+ "-fx-background-color: rgba(255, 255, 255, 0.2);" + "-fx-padding: 5px 10px;"
				+ "-fx-background-radius: 10px;");

		lblTurn.setStyle("-fx-font-size: 18px;" + "-fx-font-style: italic;" + "-fx-text-fill: #4CAF50;"
				+ "-fx-background-color: rgba(255,255,255,0.3);" + "-fx-padding: 4px 8px;"
				+ "-fx-background-radius: 10px;");
	}

	private void updateTurnLabel() {
		System.out.println("I am: " + player + ", current turn: " + boardGame.whoTurn());
		if (boardGame.whoTurn() == player) {
			lblTurn.setText("Your Turn");
			changeButtonsDisability(false);
		} else {
			lblTurn.setText("Opponent's Turn");
			changeButtonsDisability(true);
		}

		lblPlayer.setStyle("-fx-font-size: 22px;" + "-fx-font-weight: bold;"
				+ "-fx-text-fill: linear-gradient(to right, #2196F3, #21CBF3);"
				+ "-fx-background-color: rgba(255, 255, 255, 0.2);" + "-fx-padding: 5px 10px;"
				+ "-fx-background-radius: 10px;");

		lblTurn.setStyle("-fx-font-size: 18px;" + "-fx-font-style: italic;" + "-fx-text-fill: #4CAF50;"
				+ "-fx-background-color: rgba(255,255,255,0.3);" + "-fx-padding: 4px 8px;"
				+ "-fx-background-radius: 10px;");
	}

	private void changeButtonsDisability(boolean set) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				soliderImgs[i][j].setDisable(set);
			}
		}
	}

	private void updateBoard() {
		grid.getChildren().clear();
		soliderImgs = new ImageView[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ImageView img = new ImageView();

				// קבע גודל אחיד לתמונה כדי שתהיה בגודל משבצת
				img.setFitWidth(40); // אפשר לשנות ל־grid.getWidth()/8 אם רוצים דינמי
				img.setFitHeight(40);
				img.setPreserveRatio(true); // כדי לשמור על פרופורציות
				img.setPickOnBounds(true);

				BoardStateClients.CellState player = boardGame.getBoardCell(i, j);
				if (player == BoardStateClients.CellState.PLAYER1) {
					img.setImage(new Image(getClass().getResourceAsStream("/images/solider1.png")));
				} else if (player == BoardStateClients.CellState.PLAYER2) {
					img.setImage(new Image(getClass().getResourceAsStream("/images/solider2.png")));
				} else if (player == BoardStateClients.CellState.KING1) {
					img.setImage(new Image(getClass().getResourceAsStream("/images/king1.png")));
				} else if (player == BoardStateClients.CellState.KING2) {
					img.setImage(new Image(getClass().getResourceAsStream("/images/king2.png")));
				} else {
					img.setImage(new Image(getClass().getResourceAsStream("/images/empty.png")));

				}

				final int row = i;
				final int col = j;
				img.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						System.out.println("Clicked on: " + row + ", " + col);
						handleButtonPressed(row, col);
					}
				});

				soliderImgs[i][j] = img;
				GridPane.setHalignment(img, HPos.CENTER);
				GridPane.setValignment(img, VPos.CENTER);
				grid.add(img, j, i);
			}
		}
	}

	private void handleButtonPressed(int row, int col) {
		BoardStateClients.CellState cell = boardGame.getBoardCell(row, col);
		if (selectedCell == null) {
			// בחירה ראשונה – רק אם זה החייל שלך
			if ((player == BoardStateClients.CellState.PLAYER1
					&& (cell == BoardStateClients.CellState.PLAYER1 || cell == BoardStateClients.CellState.KING1))
					|| (player == BoardStateClients.CellState.PLAYER2 && (cell == BoardStateClients.CellState.PLAYER2
							|| cell == BoardStateClients.CellState.KING2))) {

				selectedCell = new int[] { row, col };

				// הסר סימון קודם אם יש
				if (selectedImage != null) {
					selectedImage.setStyle(""); // הסר אפקט מהתמונה הקודמת
				}

				// שמור את התמונה הנוכחית והוסף לה אפקט
				selectedImage = soliderImgs[row][col];
				selectedImage.setStyle("-fx-effect: dropshadow(gaussian, yellow, 15, 0.6, 0, 0);");
			}
		} else {
			// בחירה שנייה – יעד לזוז אליו
			int fromRow = selectedCell[0];
			int fromCol = selectedCell[1];
			int toRow = row;
			int toCol = col;

			System.out.println("Selected piece at: from" + fromRow + ", " + fromRow + "  To:" + toRow + "," + toCol);
			selectedCell = null; // איפוס הבחירה

			// הסר אפקט מהתמונה הנבחרת
			if (selectedImage != null) {
				selectedImage.setStyle("");
				selectedImage = null;
			}

			try {
				output.writeObject(new int[] { fromRow, fromCol, toRow, toCol });
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void connectToServer() {
		try {
			socket = new Socket("localhost", 8888);

			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());

			System.out.println("✅ Connected to server.");

			startListeningToServer();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startListeningToServer() {
		new Thread(() -> {
			try {
				// פעם אחת – קבל מי אתה
				Object received = input.readObject();
				if (received instanceof BoardStateClients.CellState) {
					player = (BoardStateClients.CellState) received;
				}

				// עכשיו רוץ בלולאה – קבל עדכונים של הלוח
				while (true) {
					received = input.readObject();

					if (received instanceof BoardStateClients board) {
						boardGame = board;

						Platform.runLater(() -> {
							updateBoard();
							updatePlayerLabel();
							updateTurnLabel();
						});
					} else if (received instanceof BoardStateClients.CellState winner) {
						Platform.runLater(() -> {
							String message = (winner == player) ? "🎉 You Won!" : "😢 You Lost!";
							JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						});
						break;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
