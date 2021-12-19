import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class MapGameController implements Initializable {
    public MapData mapData;
    public MoveChara moveChara;
    public GridPane mapGridPane;
    public GridPane itemGridPane;
    public Label scoreLabel;
    public Label timeLabel;

    public Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapData = new MapData(21, 15);
        moveChara = new MoveChara(1, 1, mapData);
        DrawMap(moveChara, mapData);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    long remainingTime = mapData.GetRemainingTime();
                    timeLabel.setText(String.valueOf(remainingTime));
                    if (remainingTime <= 0) {
                        timer.cancel();
                        OverButtonAction();
                        return;
                    }
                });
            }
        }, 0, 500);
    }

    public void DrawMap(MoveChara moveChara, MapData mapData) {
        int moveCharaPositionX = moveChara.GetPositionX();
        int moveCharaPositionY = moveChara.GetPositionY();
        int itemType = mapData.GetItemType(moveCharaPositionX, moveCharaPositionY);
        switch (itemType) {
            case MapData.ITEM_TYPE_PORTAL:
                printAction("PORTAL");
                moveChara.SetPositionX(1);
                moveChara.SetPositionY(1);
                mapData.SetItemType(moveCharaPositionX, moveCharaPositionY, MapData.ITEM_TYPE_NULL);
                break;
            default:
                if (itemType != MapData.ITEM_TYPE_NULL && itemType != MapData.ITEM_TYPE_GOAL) {
                    printAction("GET");
                    moveChara.AddItem(itemType);
                    mapData.SetItemType(moveCharaPositionX, moveCharaPositionY, MapData.ITEM_TYPE_NULL);
                }
                break;
        }

        moveCharaPositionX = moveChara.GetPositionX();
        moveCharaPositionY = moveChara.GetPositionY();
        mapGridPane.getChildren().clear();
        for (int y = 0; y < mapData.GetHeight(); y++) {
            for (int x = 0; x < mapData.GetWidth(); x++) {
                if (x == moveCharaPositionX && y == moveCharaPositionY) {
                    mapGridPane.add(moveChara.GetCharaImageView(), x, y);
                } else {
                    mapGridPane.add(mapData.GetMapItemImageView(x, y), x, y);
                }
            }
        }
        itemGridPane.getChildren().clear();
        ArrayList<Integer> itemInventory = moveChara.GetItemInventory();
        for (int i = 0; i < itemInventory.size(); i++) {
            itemGridPane.add(mapData.GetItemImageView(itemInventory.get(i)), i, 0);
        }
        scoreLabel.setText(String.valueOf(moveChara.GetScore()));

        if (itemType == MapData.ITEM_TYPE_GOAL) {
            if (moveChara.GetItemInventory().contains(MapData.ITEM_TYPE_KEY)) {
                printAction("CLEAR");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Clear!");
                alert.showAndWait();
                moveChara.AddScore(1000);
                RemapButtonAction();
            }
        }
    }

    public void KeyAction(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        System.out.println("KeyCode:" + keyCode);
        switch (keyCode) {
            case H:
            case A:
                LeftButtonAction();
                break;
            case J:
            case S:
                DownButtonAction();
                break;
            case K:
            case W:
                UpButtonAction();
                break;
            case L:
            case D:
                RightButtonAction();
                break;
            case R:
            case DELETE:
            case BACK_SPACE:
                RemapButtonAction();
                break;
            case ESCAPE:
                System.exit(0);
            default:
                break;
        }
    }

    public void UpButtonAction() {
        printAction("UP");
        moveChara.SetCharaDirection(MoveChara.TYPE_UP);
        moveChara.Move(MoveChara.TYPE_UP);
        DrawMap(moveChara, mapData);
    }

    public void DownButtonAction() {
        printAction("DOWN");
        moveChara.SetCharaDirection(MoveChara.TYPE_DOWN);
        moveChara.Move(MoveChara.TYPE_DOWN);
        DrawMap(moveChara, mapData);
    }

    public void LeftButtonAction() {
        printAction("LEFT");
        moveChara.SetCharaDirection(MoveChara.TYPE_LEFT);
        moveChara.Move(MoveChara.TYPE_LEFT);
        DrawMap(moveChara, mapData);
    }

    public void RightButtonAction() {
        printAction("RIGHT");
        moveChara.SetCharaDirection(MoveChara.TYPE_RIGHT);
        moveChara.Move(MoveChara.TYPE_RIGHT);
        DrawMap(moveChara, mapData);
    }

    public void RemapButtonAction() {
        printAction("REMAP");
        initialize(null, null);
    }

    public void OverButtonAction() {
        printAction("OVER");
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Game Over!\n" + "Score : " + String.valueOf(moveChara.GetScore()));
        alert.showAndWait();
        moveChara.ResetScore();
        mapData.ResetTimeLimit();
        RemapButtonAction();
    }

    public void func1ButtonAction(ActionEvent event) {
        System.out.println("func1: Nothing to do");
    }

    public void func2ButtonAction(ActionEvent event) {
        System.out.println("func2: Nothing to do");
    }

    public void func3ButtonAction(ActionEvent event) {
        System.out.println("func3: Nothing to do");
    }

    public void func4ButtonAction(ActionEvent event) {
        System.out.println("func4: Nothing to do");
    }

    public void printAction(String actionString) {
        System.out.println("Action: " + actionString);
    }
}
