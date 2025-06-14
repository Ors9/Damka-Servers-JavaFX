import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DamkaClient extends Application {

	// Start the application by loading the FXML file and showing the stage.
	public void start(Stage stage) throws Exception {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("Damka.fxml"));
		Scene scene = new Scene(root);
		stage.setTitle("Damka game");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	// Launch the JavaFX application.
	public static void main(String[] args) {
		launch(args);
		System.out.println();
	}

}
