import java.util.Date;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MapData {
    public static final int MAP_TYPE_SPACE = 0;
    public static final int MAP_TYPE_WALL = 1;
    private static final String mapImagePaths[] = {
            "image/space.png",
            "image/wall.png"
    };

    public static final int ITEM_TYPE_NULL = -1;
    public static final int ITEM_TYPE_GOAL = 0;
    public static final int ITEM_TYPE_BOMB = 1;
    public static final int ITEM_TYPE_KEY = 2;
    public static final int ITEM_TYPE_COIN = 3;
    public static final int ITEM_TYPE_PORTAL = 4;
    public static final int ITEM_TYPE_TIME = 5;
    public static final int ITEM_TYPE_HACK = 6;
    private static final String itemImagePaths[] = {
            "image/goal.png",
            "image/bomb.png",
            "image/key.png",
            "image/coin.png",
            "image/portal.png",
            "image/time.png",
            "image/hack.png"
    };

    public static final long RESET_TIME_LIMIT = 35;
    public static long TIME_LIMIT = 35;
    public static long TIME_PLUS = 10;

    private Image[] mapImages;
    private Image[] itemImages;
    private int[][] mapTypes;
    private int[][] itemTypes;
    private int width;
    private int height;

    private Date startDate;
    private long timeOffset;

    /**
     * MapDataを初期化する
     * 
     * @param x マップの幅
     * @param y マップの高さ
     */
    MapData(int x, int y) {
        width = x;
        height = y;

        mapImages = new Image[mapImagePaths.length];
        for (int i = 0; i < mapImagePaths.length; i++) {
            mapImages[i] = new Image(mapImagePaths[i]);
        }
        mapTypes = new int[y][x];

        itemImages = new Image[itemImagePaths.length];
        for (int i = 0; i < itemImagePaths.length; i++) {
            itemImages[i] = new Image(itemImagePaths[i]);
        }
        itemTypes = new int[y][x];

        FillMapType(MAP_TYPE_WALL);
        DigMap(1, 3);

        FillItemType(ITEM_TYPE_NULL);
        SetItemType(x - 2, y - 2, ITEM_TYPE_GOAL);
        SetItemTypeRandom(3, ITEM_TYPE_BOMB);
        SetItemTypeRandom(1, ITEM_TYPE_KEY);
        SetItemTypeRandom(3, ITEM_TYPE_COIN);
        SetItemTypeRandom(1, ITEM_TYPE_PORTAL);
        SetItemTypeRandom(3, ITEM_TYPE_TIME);
        SetItemTypeRandom(2, ITEM_TYPE_HACK);

        TIME_LIMIT -= 5;
        startDate = new Date();
        timeOffset = 0;
    }

    /**
     * アイテムをランダムに配置する
     * 
     * @param itemCount アイテムの数
     * @param itemType  ItemType
     */
    private void SetItemTypeRandom(int itemCount, int itemType) {
        for (int i = 0; i < itemCount; i++) {
            int tempX = (int) (Math.random() * width);
            int tempY = (int) (Math.random() * height);
            if (GetMapType(tempX, tempY) == MAP_TYPE_SPACE && GetItemType(tempX, tempY) == ITEM_TYPE_NULL
                    && !(tempX == 1 && tempY == 1)) {
                SetItemType(tempX, tempY, itemType);
            } else {
                i--;
            }
        }
    }

    /**
     * マップの高さを返す
     * 
     * @return
     */
    public int GetHeight() {
        return height;
    }

    /**
     * マップの幅を返す
     * 
     * @return
     */
    public int GetWidth() {
        return width;
    }

    /**
     * 残り時間を返す
     * 
     * @return
     */
    public long GetRemainingTime() {
        return MapData.TIME_LIMIT - (((new Date().getTime()) - startDate.getTime()) / 1000) + timeOffset;
    }

    /**
     * 残り時間を初期化する
     */
    public void ResetTimeLimit() {
        TIME_LIMIT = RESET_TIME_LIMIT;
    }

    /**
     * 残り時間を追加する
     * 
     * @param offset
     */
    public void AddTimeOffset(long offset) {
        timeOffset += offset;
    }

    /**
     * 座標がマップの範囲内であるか返す
     * 
     * @param x X座標
     * @param y Y座標
     * @return
     */
    public boolean CheckXY(int x, int y) {
        return (x < 0 || width <= x || y < 0 || height <= y);
    }

    /**
     * 座標のMapTypeを返す
     * 
     * @param x X座標
     * @param y Y座標
     * @return
     */
    public int GetMapType(int x, int y) {
        if (CheckXY(x, y)) {
            return -1;
        }
        return mapTypes[y][x];
    }

    /**
     * 座標のImageViewを返す
     * 
     * @param x X座標
     * @param y Y座標
     * @return
     */
    public ImageView GetMapItemImageView(int x, int y) {
        if (CheckXY(x, y)) {
            return null;
        }
        if (itemTypes[y][x] == MapData.ITEM_TYPE_NULL) {
            return new ImageView(mapImages[mapTypes[y][x]]);
        } else {
            return new ImageView(itemImages[itemTypes[y][x]]);
        }
    }

    /**
     * 座標のMapTypeを設定する
     * 
     * @param x       X座標
     * @param y       Y座標
     * @param mapType MapType
     */
    public void SetMapType(int x, int y, int mapType) {
        if (CheckXY(x, y)) {
            return;
        }
        mapTypes[y][x] = mapType;
    }

    /**
     * 座標のItemTypeを返す
     * 
     * @param x X座標
     * @param y Y座標
     * @return
     */
    public int GetItemType(int x, int y) {
        if (CheckXY(x, y)) {
            return -1;
        }
        return itemTypes[y][x];
    }

    /**
     * 座標のItemTypeを設定する
     * 
     * @param x        X座標
     * @param y        Y座標
     * @param itemType ItemType
     */
    public void SetItemType(int x, int y, int itemType) {
        if (CheckXY(x, y)) {
            return;
        }
        itemTypes[y][x] = itemType;
    }

    /**
     * ItemTypeのImageViewを返す
     * 
     * @param itemType ItemType
     * @return
     */
    public ImageView GetItemImageView(int itemType) {
        return new ImageView(itemImages[itemType]);
    }

    /**
     * MapTypeで全マス埋める
     * 
     * @param mapType MapType
     */
    public void FillMapType(int mapType) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapTypes[y][x] = mapType;
            }
        }
    }

    /**
     * ItemTypeで全マス埋める
     * 
     * @param itemType ItemType
     */
    public void FillItemType(int itemType) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                itemTypes[y][x] = itemType;
            }
        }
    }

    /**
     * マップ生成の処理をする
     * 
     * @param x X座標
     * @param y Y座標
     */
    public void DigMap(int x, int y) {
        SetMapType(x, y, MAP_TYPE_SPACE);
        int[] temp;
        int[][] tempVectors = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int i = 0; i < tempVectors.length; i++) {
            int j = (int) (Math.random() * tempVectors.length);
            temp = tempVectors[i];
            tempVectors[i] = tempVectors[j];
            tempVectors[j] = temp;
        }

        for (int i = 0; i < tempVectors.length; i++) {
            int dx = tempVectors[i][0];
            int dy = tempVectors[i][1];
            if (GetMapType(x + dx * 2, y + dy * 2) == MAP_TYPE_WALL) {
                SetMapType(x + dx, y + dy, MAP_TYPE_SPACE);
                DigMap(x + dx * 2, y + dy * 2);
            }
        }
    }
}
