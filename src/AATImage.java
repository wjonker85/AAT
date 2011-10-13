import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/13/11
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class AATImage {

    private int type;
    private File image;
    static int PUSH = 0;
    static int PULL = 1;

    public AATImage(File image, int type) {
        this.type = type;
        this.image = image;

    }

    public int getType() {
        return type;
    }

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
