import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Date;

public class MapData {
    public static final int MAP_TYPE_SPACE = 0;
    public static final int MAP_TYPE_WALL = 1;
    private static final String mapImagePaths[] = {
            "image/space.png",
            "image/wall.png"
    };

    public static final int ITEM_TYPE_NULL = -1;
    public static final int ITEM_TYPE_GOAL = 0;
    public static final int ITEM_TYPE_KEY = 1;
    public static final int ITEM_TYPE_PORTAL = 2;
    private static final String itemImagePaths[] = {
            "image/goal.png",
            "image/key.png",
            "image/portal.png"
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
        SetItemTypeRandom(1, ITEM_TYPE_KEY);
        SetItemTypeRandom(1, ITEM_TYPE_PORTAL);

        TIME_LIMIT -= 5;
        startDate = new Date();
        timeOffset = 0;
    }

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

    public int GetHeight() {
        return height;
    }

    public int GetWidth() {
        return width;
    }

    public long GetRemainingTime() {
        return MapData.TIME_LIMIT - (((new Date().getTime()) - startDate.getTime()) / 1000) + timeOffset;
    }

    public void ResetTimeLimit() {
        TIME_LIMIT = RESET_TIME_LIMIT;
    }

    public void AddTimeOffset(long offset) {
        timeOffset += offset;
    }

    public boolean CheckXY(int x, int y) {
        return (x < 0 || width <= x || y < 0 || height <= y);
    }

    public int GetMapType(int x, int y) {
        if (CheckXY(x, y)) {
            return -1;
        }
        return mapTypes[y][x];
    }

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

    public void SetMapType(int x, int y, int mapType) {
        if (CheckXY(x, y)) {
            return;
        }
        mapTypes[y][x] = mapType;
    }

    public int GetItemType(int x, int y) {
        if (CheckXY(x, y)) {
            return -1;
        }
        return itemTypes[y][x];
    }

    public void SetItemType(int x, int y, int itemType) {
        if (CheckXY(x, y)) {
            return;
        }
        itemTypes[y][x] = itemType;
    }

    public ImageView GetItemImageView(int itemType) {
        return new ImageView(itemImages[itemType]);
    }

    public void FillMapType(int mapType) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapTypes[y][x] = mapType;
            }
        }
    }

    public void FillItemType(int itemType) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                itemTypes[y][x] = itemType;
            }
        }
    }

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
