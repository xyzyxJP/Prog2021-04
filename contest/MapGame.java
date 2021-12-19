import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MapGame extends Application {
    Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("MAP GAME");
        Pane pane = (Pane) FXMLLoader.load(getClass().getResource("fxml/MapGame.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
