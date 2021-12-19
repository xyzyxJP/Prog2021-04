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

    MoveChara(int startX, int startY, MapData mapData) {
        this.mapData = mapData;

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

    public boolean CanMove(int charaDirection) {
        if (mapData.GetMapType(positionX + VECTORS[charaDirection][1],
                positionY + VECTORS[charaDirection][0]) == MapData.MAP_TYPE_SPACE) {
            return true;
        }
        return false;
    }

    public boolean Move(int charaDirection) {
        if (CanMove(charaDirection)) {
            positionX += VECTORS[charaDirection][1];
            positionY += VECTORS[charaDirection][0];
            return true;
        } else {
            return false;
        }
    }

    public ImageView GetCharaImageView() {
        return charaImageViews[charaDirection];
    }

    public int GetPositionX() {
        return positionX;
    }

    public int GetPositionY() {
        return positionY;
    }

    public void SetPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void SetPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void AddItem(int itemType) {
        switch (itemType) {
            default:
                AddScore(50);
                itemInventory.add(itemType);
                break;
        }
    }

    public ArrayList<Integer> GetItemInventory() {
        return itemInventory;
    }

    public boolean UseItem(int itemType) {
        if (!itemInventory.contains(itemType)) {
            return false;
        }
        switch (itemType) {
            default:
                break;
        }
        return false;
    }

    public int GetScore() {
        return score;
    }

    public void AddScore(int score) {
        MoveChara.score += score;
    }

    public void ResetScore() {
        MoveChara.score = 0;
    }

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
