import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/13/11
 * This class contains an image together with the required information belonging to that image
 * Whether the image needs to be pushed or pulled and if it's a affective or neutral image (Needed for data analysis).
 */
public class AATImage {

    private int type;
    private int direction;
    private File image;
    static int PUSH = 0;
    static int PULL = 1;
    static int NEUTRAL = 0;
    static int AFFECTIVE = 1;

    public AATImage(File image, int direction, int type) {
        this.direction = direction;
        this.type = type;
        this.image = image;

    }


    //Returns whether it's a pull or push image
    public int getDirection() {
        return direction;
    }

    //Returns whether it's a neutral or affective image
    public int getType() {
        return type;
    }

    //Gets the image filename
    public String toString() {
        return image.getName();
    }

    //Loads the requested image
    public BufferedImage getImage() {
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return bufImage;
    }
}
