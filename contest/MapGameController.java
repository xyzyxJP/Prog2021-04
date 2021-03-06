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
import javafx.scene.media.AudioClip;

public class MapGameController implements Initializable {
    public RankData rankData;
    public MapData mapData;
    public MoveChara moveChara;
    public GridPane mapGridPane;
    public GridPane itemGridPane;
    public Label scoreLabel;
    public Label timeLabel;
    public AudioClip mainAudioClip;
    public AudioClip itemAudioClip;
    public AudioClip coinAudioClip;
    public AudioClip portalAudioClip;
    public AudioClip bombAudioClip;
    public AudioClip hackAudioClip;
    public AudioClip clearAudioClip;
    public Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 音声ファイルを読み込む
        mainAudioClip = new AudioClip(getClass().getResource("audio/main.mp3").toExternalForm());
        mainAudioClip.setCycleCount(AudioClip.INDEFINITE);
        mainAudioClip.setVolume(0.02);
        mainAudioClip.play();
        itemAudioClip = new AudioClip(getClass().getResource("audio/item.mp3").toExternalForm());
        itemAudioClip.setCycleCount(1);
        itemAudioClip.setVolume(0.02);
        coinAudioClip = new AudioClip(getClass().getResource("audio/coin.mp3").toExternalForm());
        coinAudioClip.setCycleCount(1);
        coinAudioClip.setVolume(0.02);
        portalAudioClip = new AudioClip(getClass().getResource("audio/portal.mp3").toExternalForm());
        portalAudioClip.setCycleCount(1);
        portalAudioClip.setVolume(0.02);
        bombAudioClip = new AudioClip(getClass().getResource("audio/bomb.mp3").toExternalForm());
        bombAudioClip.setCycleCount(1);
        bombAudioClip.setVolume(0.05);
        hackAudioClip = new AudioClip(getClass().getResource("audio/hack.mp3").toExternalForm());
        hackAudioClip.setCycleCount(1);
        hackAudioClip.setVolume(0.05);
        clearAudioClip = new AudioClip(getClass().getResource("audio/clear.mp3").toExternalForm());
        clearAudioClip.setCycleCount(1);
        clearAudioClip.setVolume(0.05);
        // ランクを読み込む
        rankData = new RankData();
        // マップを初期化する
        RemapButtonAction();
    }

    /**
     * マップを描画する
     * 
     * @param moveChara MoveChara
     * @param mapData   MapData
     */
    public void DrawMap(MoveChara moveChara, MapData mapData) {
        // キャラクターのX, Y座標を取得する
        int moveCharaPositionX = moveChara.GetPositionX();
        int moveCharaPositionY = moveChara.GetPositionY();
        // キャラクターがいる場所のitemTypeを取得する
        int itemType = mapData.GetItemType(moveCharaPositionX, moveCharaPositionY);
        switch (itemType) {
            case MapData.ITEM_TYPE_PORTAL:
                // ポータルである場合は1, 1にキャラクターを移動させる
                PrintAction("PORTAL");
                portalAudioClip.play();
                moveChara.SetPositionX(1);
                moveChara.SetPositionY(1);
                // キャラクターがいる場所のitemTypeを空にする
                mapData.SetItemType(moveCharaPositionX, moveCharaPositionY, MapData.ITEM_TYPE_NULL);
                break;
            default:
                // 空ではないかつゴールではない場合はアイテムインベントリに追加する
                if (itemType != MapData.ITEM_TYPE_NULL && itemType != MapData.ITEM_TYPE_GOAL) {
                    PrintAction("GET");
                    if (itemType == MapData.ITEM_TYPE_COIN) {
                        coinAudioClip.play();
                    } else {
                        itemAudioClip.play();
                    }
                    moveChara.AddItem(itemType);
                    // キャラクターがいる場所のitemTypeを空にする
                    mapData.SetItemType(moveCharaPositionX, moveCharaPositionY, MapData.ITEM_TYPE_NULL);
                }
                break;
        }
        // キャラクターのX, Y座標を更新する
        moveCharaPositionX = moveChara.GetPositionX();
        moveCharaPositionY = moveChara.GetPositionY();
        // マップの画像を描画する
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
        // アイテムインベントリの画像を描画する
        itemGridPane.getChildren().clear();
        ArrayList<Integer> itemInventory = moveChara.GetItemInventory();
        for (int i = 0; i < itemInventory.size(); i++) {
            itemGridPane.add(mapData.GetItemImageView(itemInventory.get(i)), i, 0);
        }
        // スコアを表示する
        scoreLabel.setText(String.valueOf(moveChara.GetScore()));
        // ゴールである場合は次のマップへの処理を行う
        if (itemType == MapData.ITEM_TYPE_GOAL) {
            if (moveChara.GetItemInventory().contains(MapData.ITEM_TYPE_KEY)) {
                PrintAction("CLEAR");
                timer.cancel();
                // 残り時間は引き継がれないためスコアとして加算する
                moveChara.AddScore(1000 + 10 * (int) mapData.GetRemainingTime());
                clearAudioClip.play();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Clear!");
                alert.showAndWait();
                RemapButtonAction();
            }
        }
    }

    /**
     * キー入力時の処理をする
     * 
     * @param keyEvent KeyEvent
     */
    public void KeyAction(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        System.out.println("KeyCode:" + keyCode);
        switch (keyCode) {
            case A:
                LeftButtonAction();
                break;
            case S:
                DownButtonAction();
                break;
            case W:
                UpButtonAction();
                break;
            case D:
                RightButtonAction();
                break;
            case B:
                BombButtonAction(null);
                break;
            case H:
                HackButtonAction(null);
                break;
            case R:
            case DELETE:
            case BACK_SPACE:
                OverButtonAction(null);
                break;
            case ESCAPE:
                System.exit(0);
            default:
                break;
        }
    }

    /**
     * 上へ移動する
     */
    public void UpButtonAction() {
        PrintAction("UP");
        moveChara.SetCharaDirection(MoveChara.TYPE_UP);
        moveChara.Move(MoveChara.TYPE_UP, 1);
        DrawMap(moveChara, mapData);
    }

    /**
     * 下へ移動する
     */
    public void DownButtonAction() {
        PrintAction("DOWN");
        moveChara.SetCharaDirection(MoveChara.TYPE_DOWN);
        moveChara.Move(MoveChara.TYPE_DOWN, 1);
        DrawMap(moveChara, mapData);
    }

    /**
     * 左へ移動する
     */
    public void LeftButtonAction() {
        PrintAction("LEFT");
        moveChara.SetCharaDirection(MoveChara.TYPE_LEFT);
        moveChara.Move(MoveChara.TYPE_LEFT, 1);
        DrawMap(moveChara, mapData);
    }

    /**
     * 右へ移動する
     */
    public void RightButtonAction() {
        PrintAction("RIGHT");
        moveChara.SetCharaDirection(MoveChara.TYPE_RIGHT);
        moveChara.Move(MoveChara.TYPE_RIGHT, 1);
        DrawMap(moveChara, mapData);
    }

    /**
     * マップを初期化する
     */
    public void RemapButtonAction() {
        PrintAction("REMAP");
        // マップを生成する
        mapData = new MapData(21, 15);
        // キャラクターを生成する
        moveChara = new MoveChara(1, 1, mapData);
        // マップを描画する
        DrawMap(moveChara, mapData);
        // 制限時間表示タイマーを初期化する
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        // 0.5秒おきに残り時間を取得し表示する
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    long remainingTime = mapData.GetRemainingTime();
                    timeLabel.setText(String.valueOf(remainingTime));
                    if (remainingTime <= 0) {
                        OverButtonAction(null);
                        return;
                    }
                });
            }
        }, 0, 500);
    }

    /**
     * 爆弾を使用する
     * 
     * @param actionEvent ActionEvent
     */
    public void BombButtonAction(ActionEvent actionEvent) {
        PrintAction("BOMB");
        if (moveChara.UseItem(MapData.ITEM_TYPE_BOMB)) {
            bombAudioClip.play();
        }
        DrawMap(moveChara, mapData);
    }

    /**
     * 一方通行を使用する
     * 
     * @param actionEvent ActionEvent
     */
    public void HackButtonAction(ActionEvent actionEvent) {
        PrintAction("HACK");
        if (moveChara.UseItem(MapData.ITEM_TYPE_HACK)) {
            hackAudioClip.play();
        }
        DrawMap(moveChara, mapData);
    }

    /**
     * ゲームオーバーの処理をする
     */
    public void OverButtonAction(ActionEvent actionEvent) {
        PrintAction("OVER");
        timer.cancel();
        Rank rank = rankData.SubmitScore(moveChara.GetScore());
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Game Over!\n" + "Score : " + rank.score + "\n" + "Rank : " + rank.rank);
        alert.showAndWait();
        // キャラクターのスコアを初期化する
        moveChara.ResetScore();
        // 次の残り時間を初期化する
        mapData.ResetTimeLimit();
        RemapButtonAction();
        mainAudioClip.stop();
        mainAudioClip.play();
    }

    /**
     * ランクを表示する
     */
    public void RankButtonAction(ActionEvent actionEvent) {
        PrintAction("RANK");
        timer.cancel();
        String rankText = "";
        // 10位までのランクを文字列に追加する
        ArrayList<Rank> rankList = rankData.GetRankList();
        for (int i = 0; i < Math.min(rankList.size(), 10); i++) {
            rankText += String.valueOf(i + 1) + " | " + rankList.get(i) + "\n";
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Ranking\n" + rankText);
        alert.showAndWait();
        // キャラクターのスコアを初期化する
        moveChara.ResetScore();
        // 次の残り時間を初期化する
        mapData.ResetTimeLimit();
        RemapButtonAction();
        mainAudioClip.stop();
        mainAudioClip.play();
    }

    /**
     * アクションログを表示する
     * 
     * @param actionString アクション名
     */
    public void PrintAction(String actionString) {
        System.out.println("Action: " + actionString);
    }
}
