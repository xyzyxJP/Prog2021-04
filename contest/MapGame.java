import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class MapGame extends Application {
    Stage stage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
	stage = primaryStage;
	primaryStage.setTitle("MAP GAME");
	Pane myPane_top = (Pane)FXMLLoader.load(getClass().getResource("MapGame.fxml"));
	Scene myScene = new Scene(myPane_top);
	primaryStage.setScene(myScene);
    primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
