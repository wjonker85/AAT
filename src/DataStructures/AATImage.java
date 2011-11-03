/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package DataStructures;

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
    private String name;
    private BufferedImage image;
    public static int PUSH = 0;
    public static int PULL = 1;
    public static int NEUTRAL = 0;
    public static int AFFECTIVE = 1;
    public static int PRACTICE = 2;

    public AATImage(File imageFile, int direction, int type) {
        this.direction = direction;
        this.type = type;
        name = imageFile.getName();
        this.image = loadImage(imageFile);

    }

    //Create a generated AAT Image. This creates an image that is just a square with the given color.
    public AATImage(int direction, Color color, int nr) {
        this.direction = direction;
        this.image = getPracticeImage(color);
        this.type = AATImage.PRACTICE;
        this.name = "Practice_"+nr;
    }


    //Returns whether it's a pull or push image
    public int getDirection() {
        return direction;
    }

    private BufferedImage getPracticeImage(Color color) {
        BufferedImage PracticeImage = new BufferedImage(433, 433, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = PracticeImage.createGraphics();
        g.setBackground(color);
        g.setColor(color);
        g.fillRect(0, 0, 433, 433);
        return PracticeImage;
    }

    //Returns whether it's a neutral or affective image
    public int getType() {
        return type;
    }

    //Gets the image filename
    public String toString() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }

    //Loads the requested image
    public BufferedImage loadImage(File imageFile) {
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return bufImage;
    }
}
