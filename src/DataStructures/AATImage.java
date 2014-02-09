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
    private boolean hasBorders;
    private Color borderColor;
    private int borderWidth;

    public AATImage(File imageFile, int direction, int type, AatObject aatObject) {
        this.direction = direction;
        this.type = type;
        hasBorders = aatObject.hasColoredBorders();
        if (hasBorders) {
            int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
            borderColor = new Color(intValue);
            borderWidth = aatObject.getBorderWidth();
        }

        name = imageFile.getName();
        System.out.println("Adding image 1 "+imageFile.getAbsoluteFile());
        this.image = loadImage(imageFile, aatObject.getImageSizePerc());


    }

    public AATImage(File imageFile, int direction, AatObject aatObject, int repeat) {
        this.direction = direction;
        this.type = PRACTICE;
        hasBorders = aatObject.hasColoredBorders();
        if (hasBorders) {
            int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
            borderColor = new Color(intValue);
            borderWidth = aatObject.getBorderWidth();
            name = imageFile.getName() + "_" + repeat;
        }
        else {
            name = imageFile.getName();
        }

        System.out.println("Adding image 2 "+name);
        this.image = loadImage(imageFile, aatObject.getImageSizePerc());


    }

    //Create a generated AAT Image. This creates an image that is just a square with the given color.
    public AATImage(int direction, Color color, int nr, AatObject aatObject) {
        this.direction = direction;
        hasBorders = aatObject.hasColoredBorders();
        int intValue = Integer.parseInt(aatObject.getBorderColor(direction), 16);
        borderColor = new Color(intValue);
        borderWidth = aatObject.getBorderWidth();
        this.image = getPracticeImage(color, aatObject.getImageSizePerc());
        this.type = AATImage.PRACTICE;
        this.name = "practice_" + nr;
    }


    //Returns whether it's a pull or push image
    public int getDirection() {
        return direction;
    }

    //Create a practice image. This is just a rect with a single fill color. Ik also resizes the image dependent of the screenSize
    //And the number of steps needed to resize the image
    private BufferedImage getPracticeImage(Color color, int startPerc) {
        float startSize = (float) startPerc / 100f;
        BufferedImage practiceImage = new BufferedImage(433, 433, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = practiceImage.createGraphics();
        g.setBackground(color);
        g.setColor(color);
        g.fillRect(0, 0, 433, 433);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        practiceImage = ImageUtils.resizeImageWithHint(practiceImage, (int) ((startSize * dim.getHeight()) - (borderWidth * 2)), (int) ((startSize * dim.getHeight()) - borderWidth), BufferedImage.TYPE_INT_ARGB);
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
    private BufferedImage loadImage(File imageFile, int startPerc) {
        float startSize = (float) startPerc / 100f;
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        assert bufImage != null;
        int bWidth = 0;
        if (hasBorders) {
            bWidth = borderWidth * 2;
        }

        //Resize naar juiste schermgrootte
        if (bufImage.getWidth() > bufImage.getHeight()) {
            double resizeFactor = (startSize * dim.getHeight()) / bufImage.getWidth();
            int newHeight = (int) (bufImage.getHeight() * resizeFactor) - bWidth;
            bufImage = ImageUtils.resizeImageWithHint(bufImage, (int) (startSize * dim.getHeight()) - bWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        } else {
            double resizeFactor = startSize * (dim.getHeight()) / bufImage.getHeight();
            int newWidth = (int) (bufImage.getWidth() * resizeFactor) - bWidth;
            bufImage = ImageUtils.resizeImageWithHint(bufImage, newWidth, (int) (startSize * dim.getHeight()) - bWidth, BufferedImage.TYPE_INT_ARGB);
        }
        if (hasBorders) {
            bufImage = ImageUtils.drawBorder(bufImage, borderColor, borderWidth);
        }
        return bufImage;
    }
}
