package application;

	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/popup_register.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("처리업체 등록");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }	
	}
	public static void main(String[] args) {
		launch(args);
	}
}
