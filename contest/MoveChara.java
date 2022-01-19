import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MoveChara {
    public static final int[][] VECTORS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    public static final int TYPE_UP = 0;
    public static final int TYPE_DOWN = 1;
    public static final int TYPE_LEFT = 2;
    public static final int TYPE_RIGHT = 3;

    private final String[] directions = { "up", "down", "left", "right" };
    private final String[] animationNumbers = { "1", "2", "3" };
    private final String imagePathCat = "image/cat/";
    private final String imagePathExt = ".png";

    private int positionX;
    private int positionY;

    private static int score = 0;

    private ArrayList<Integer> itemInventory = new ArrayList<Integer>();

    private MapData mapData;

    private Image[][] charaImages;
    private ImageView[] charaImageViews;
    private ImageAnimation[] charaImageAnimations;

    private int charaDirection;

    /**
     * MoveCharaを初期化する
     * 
     * @param startX  初期X座標
     * @param startY  初期Y座標
     * @param mapData MapData
     */
    MoveChara(int startX, int startY, MapData mapData) {
        this.mapData = mapData;
        // キャラクターの画像を読み込む
        charaImages = new Image[4][3];
        charaImageViews = new ImageView[4];
        charaImageAnimations = new ImageAnimation[4];
        for (int i = 0; i < 4; i++) {
            charaImages[i] = new Image[3];
            for (int j = 0; j < 3; j++) {
                charaImages[i][j] = new Image(imagePathCat + directions[i] + animationNumbers[j] + imagePathExt);
            }
            charaImageViews[i] = new ImageView(charaImages[i][0]);
            charaImageAnimations[i] = new ImageAnimation(charaImageViews[i], charaImages[i]);
        }
        positionX = startX;
        positionY = startY;
        SetCharaDirection(TYPE_RIGHT);
    }

    /**
     * キャラクターの向きを変更する
     * 
     * @param charaDirection キャラクターの向き
     */
    public void SetCharaDirection(int charaDirection) {
        this.charaDirection = charaDirection;
        for (int i = 0; i < 4; i++) {
            if (i == charaDirection) {
                charaImageAnimations[i].start();
            } else {
                charaImageAnimations[i].stop();
            }
        }
    }

    /**
     * 指定方向に移動できるかを返す
     * 
     * @param charaDirection キャラクターの向き
     * @param distance       キャラクターからの距離
     * @return 移動できるか
     */
    public boolean CanMove(int charaDirection, int distance) {
        if (mapData.CheckXY(positionX + VECTORS[charaDirection][1] * distance,
                positionY + VECTORS[charaDirection][0] * distance)) {
            return false;
        }
        if (mapData.GetMapType(positionX + VECTORS[charaDirection][1] * distance,
                positionY + VECTORS[charaDirection][0] * distance) == MapData.MAP_TYPE_SPACE) {
            return true;
        }
        return false;
    }

    /**
     * 指定方向に移動する
     * 
     * @param charaDirection キャラクターの向き
     * @param distance       キャラクターからの距離
     * @return 移動できたか
     */
    public boolean Move(int charaDirection, int distance) {
        if (CanMove(charaDirection, distance)) {
            positionX += VECTORS[charaDirection][1] * distance;
            positionY += VECTORS[charaDirection][0] * distance;
            return true;
        } else {
            return false;
        }
    }

    /**
     * キャラクターのImageViewを返す
     * 
     * @return ImageView
     */
    public ImageView GetCharaImageView() {
        return charaImageViews[charaDirection];
    }

    /**
     * キャラクターのX座標を返す
     * 
     * @return X座標
     */
    public int GetPositionX() {
        return positionX;
    }

    /**
     * キャラクターのY座標を返す
     * 
     * @return Y座標
     */
    public int GetPositionY() {
        return positionY;
    }

    /**
     * キャラクターのX座標を設定する
     * 
     * @param positionX X座標
     */
    public void SetPositionX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * キャラクターのY座標を設定する
     * 
     * @param positionY Y座標
     */
    public void SetPositionY(int positionY) {
        this.positionY = positionY;
    }

    /**
     * キャラクターのイベントリにアイテムを追加する
     * 
     * @param itemType ItemType
     */
    public void AddItem(int itemType) {
        switch (itemType) {
            case MapData.ITEM_TYPE_COIN:
                AddScore(200);
                break;
            case MapData.ITEM_TYPE_TIME:
                AddScore(50);
                mapData.AddTimeOffset(MapData.TIME_PLUS);
                break;
            default:
                AddScore(50);
                itemInventory.add(itemType);
                break;
        }
    }

    /**
     * キャラクターのイベントリ一覧を返す
     * 
     * @return インベントリ一覧
     */
    public ArrayList<Integer> GetItemInventory() {
        return itemInventory;
    }

    /**
     * キャラクターのアイテムを使用する
     * 
     * @param itemType ItemType
     * @return 使用できたか
     */
    public boolean UseItem(int itemType) {
        // アイテムを所持しているかを確認する
        if (!itemInventory.contains(itemType)) {
            return false;
        }
        switch (itemType) {
            case MapData.ITEM_TYPE_BOMB:
                // キャラクターが向いてる方向に壁があるかを確認する
                if (!CanMove(charaDirection, 1)) {
                    itemInventory.remove(itemInventory.indexOf(itemType));
                    mapData.SetMapType(positionX + VECTORS[charaDirection][1],
                            positionY + VECTORS[charaDirection][0], MapData.MAP_TYPE_SPACE);
                    AddScore(100);
                    return true;
                }
                break;
            case MapData.ITEM_TYPE_HACK:
                // キャラクターが向いている方向に壁があり、その奥が移動可能であるか確認する
                if (!CanMove(charaDirection, 1) && CanMove(charaDirection, 2)) {
                    itemInventory.remove(itemInventory.indexOf(itemType));
                    Move(charaDirection, 2);
                    AddScore(100);
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * キャラクターのスコアを返す
     * 
     * @return スコア
     */
    public int GetScore() {
        return score;
    }

    /**
     * キャラクターのスコアを追加する
     * 
     * @param score スコア
     */
    public void AddScore(int score) {
        MoveChara.score += score;
    }

    /**
     * キャラクターのスコアを初期化する
     */
    public void ResetScore() {
        MoveChara.score = 0;
    }

    /**
     * キャラクターのアニメーション処理をする
     */
    private class ImageAnimation extends AnimationTimer {
        private ImageView charaView = null;
        private Image[] charaImages = null;
        private int index = 0;

        private long duration = 500 * 1000000L;
        private long startTime = 0;

        private long count = 0L;
        private long preCount;
        private boolean isPlus = true;

        public ImageAnimation(ImageView charaView, Image[] images) {
            this.charaView = charaView;
            this.charaImages = images;
            this.index = 0;
        }

        @Override
        public void handle(long now) {
            if (startTime == 0) {
                startTime = now;
            }
            preCount = count;
            count = (now - startTime) / duration;
            if (preCount != count) {
                if (isPlus) {
                    index++;
                } else {
                    index--;
                }
                if (index < 0 || 2 < index) {
                    index = 1;
                    isPlus = !isPlus;
                }
                charaView.setImage(charaImages[index]);
            }
        }
    }
}
