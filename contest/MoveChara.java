import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;

public class MoveChara {
    public static final int TYPE_DOWN  = 0;
    public static final int TYPE_LEFT  = 1;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_UP    = 3;

    private final String[] directions  = { "Down", "Left", "Right", "Up" };
    private final String[] animationNumbers = { "1", "2", "3" };
    private final String pngPathBefore = "png/cat";
    private final String pngPathAfter  = ".png";

    private int posX;
    private int posY;

    private MapData mapData;

    private Image[][] charaImages;
    private ImageView[] charaImageViews;
    private ImageAnimation[] charaImageAnimations;

	private int charaDirection;

    MoveChara(int startX, int startY, MapData mapData){
        this.mapData = mapData;

        charaImages = new Image[4][3];
        charaImageViews = new ImageView[4];
        charaImageAnimations = new ImageAnimation[4];

        for (int i=0; i<4; i++) {
            charaImages[i] = new Image[3];
            for (int j=0; j<3; j++) {
                charaImages[i][j] = new Image(pngPathBefore + directions[i] + animationNumbers[j] + pngPathAfter);
            }
            charaImageViews[i] = new ImageView(charaImages[i][0]);
            charaImageAnimations[i] = new ImageAnimation( charaImageViews[i], charaImages[i] );
        }

        posX = startX;
        posY = startY;

        setCharaDirection(TYPE_RIGHT); // start from the image of right-direction
    }

    // set the cat's image of a direction
    public void setCharaDirection(int cd){
        charaDirection = cd;
        for (int i=0; i<4; i++) {
            if (i == charaDirection) {
                charaImageAnimations[i].start();
            } else {
                charaImageAnimations[i].stop();
            }
        }
    }

	// check the place where the cat will go
    public boolean isMovable(int dx, int dy){
        if (mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_WALL){
            return false;
        } else if (mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_SPACE){
            return true;
        }
        return false;
    }

    // move the cat
    public boolean move(int dx, int dy){
        if (isMovable(dx,dy)){
            posX += dx;
            posY += dy;
            return true;
        }else {
            return false;
        }
    }

    public ImageView getCharaImageView(){
        return charaImageViews[charaDirection];
    }

    // getter: x-positon of the cat
    public int getPosX(){
        return posX;
    }

    // getter: y-positon of the cat
    public int getPosY(){
        return posY;
    }

    // Draw the cat animation
    private class ImageAnimation extends AnimationTimer {

        private ImageView charaView = null;
        private Image[] charaImages = null;
        private int index = 0;

        private long duration = 500 * 1000000L;   // 500[ms]
        private long startTime = 0;

        private long count = 0L;
        private long preCount;
        private boolean isPlus = true;

        public ImageAnimation( ImageView charaView , Image[] images ) {
            this.charaView = charaView;
            this.charaImages = images;
            this.index = 0;
        }

        @Override
        public void handle( long now ) {
            if( startTime == 0 ){ startTime = now; }

            preCount = count;
            count  = ( now - startTime ) / duration;
            if (preCount != count) {
                if (isPlus) {
                    index++;
                } else {
                    index--;
                }
                if ( index < 0 || 2 < index ) {
                    index = 1;
                    isPlus = !isPlus; // true == !false, false == !true
                }
                charaView.setImage(charaImages[index]);
            }
        }
    }
}
