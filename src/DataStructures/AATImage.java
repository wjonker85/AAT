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

import AAT.AatObject;
import AAT.Util.ImageUtils;

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
 * It also prepares the image before it has to be shown. This way no more operations are required on the images before showing them on screen.
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
    private int stepSize;
    private boolean hasBorders;
    private Color borderColor;
    private int borderWidth;

    public AATImage(File imageFile, int direction, int type, AatObject aatObject) {
        this.direction = direction;
        this.type = type;
        this.stepSize = aatObject.getStepRate();
        hasBorders = aatObject.hasColoredBorders();
        if (hasBorders) {
            int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
            borderColor = new Color(intValue);
            borderWidth = aatObject.getBorderWidth();
        }

        name = imageFile.getName();

        this.image = loadImage(imageFile);


    }

    public AATImage(File imageFile, int direction, AatObject aatObject, int repeat) {
        this.direction = direction;
        this.type = PRACTICE;
        this.stepSize = aatObject.getStepRate();
        hasBorders = aatObject.hasColoredBorders();
        if (hasBorders) {
            int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
            borderColor = new Color(intValue);
            borderWidth = aatObject.getBorderWidth();
        }
        name = imageFile.getName() + "_" + repeat;

        this.image = loadImage(imageFile);


    }

    //Create a generated AAT Image. This creates an image that is just a square with the given color.
    public AATImage(int direction, Color color, int nr, AatObject aatObject) {
        this.direction = direction;
        this.stepSize = aatObject.getStepRate();
        hasBorders = aatObject.hasColoredBorders();
        int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
        borderColor = new Color(intValue);
        borderWidth = aatObject.getBorderWidth();
        this.image = getPracticeImage(color);
        this.type = AATImage.PRACTICE;
        this.name = "practice_" + nr;
    }


    //Returns whether it's a pull or push image
    public int getDirection() {
        return direction;
    }

    //Create a practice image. This is just a rect with a single fill color. Ik also resizes the image dependent of the screenSize
    //And the number of steps needed to resize the image
    private BufferedImage getPracticeImage(Color color) {
        BufferedImage practiceImage = new BufferedImage(433, 433, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = practiceImage.createGraphics();
        g.setBackground(color);
        g.setColor(color);
        g.fillRect(0, 0, 433, 433);
        int stepStart = Math.round(stepSize / 2f);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = ImageUtils.setupImage((int) dim.getHeight(), (int) dim.getWidth(), practiceImage.getHeight(), practiceImage.getWidth(), stepStart);
        practiceImage = ImageUtils.resizeImageWithHint(practiceImage, d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        practiceImage = ImageUtils.drawBorder(practiceImage, borderColor, borderWidth);
        return practiceImage;
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

    //Loads the requested image and resizes it to the screenSize
    private BufferedImage loadImage(File imageFile) {
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int stepStart = Math.round(stepSize / 2f);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        assert bufImage != null;
        Dimension d = ImageUtils.setupImage((int) dim.getHeight(), (int) dim.getWidth(), bufImage.getHeight(), bufImage.getWidth(), stepStart);
        //   Dimension d = ImageUtils.setupImage((int) dim.getHeight(),(int) dim.getHeight(), bufImage.getHeight(), bufImage.getWidth(), stepStart);

        bufImage = ImageUtils.resizeImageWithHint(bufImage, d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        if (hasBorders) {
            bufImage = ImageUtils.drawBorder(bufImage, borderColor, borderWidth);
        }
        return bufImage;
    }
}
