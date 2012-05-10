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

package AAT.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 2/22/12
 * Time: 8:26 PM
 * Static helper class for the images. For resizing, creating borders
 */
public class ImageUtils {


    public static BufferedImage drawBorder(BufferedImage originalImage, Color borderColor, int borderWidth) {
        BufferedImage bi = new BufferedImage(originalImage.getWidth() + (2 * borderWidth), originalImage.getHeight() + (2 * borderWidth), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(borderColor);
        g.drawImage(originalImage, borderWidth, borderWidth, null);
        BasicStroke stroke = new BasicStroke(borderWidth * 2);
        g.setStroke(stroke);
        g.drawRect(0, 0, bi.getWidth(), bi.getHeight());
        g.dispose();
        return bi;
    }

    /*
   Probeersel om een image te resizen
    */
    public static BufferedImage resizeImageWithHint(BufferedImage originalImage, int imageWidth, int imageHeight, int type) {
        if (imageWidth <= 0) {
            imageWidth = 1;
        }
        if (imageHeight <= 0) {
            imageHeight = 1;
        }
        BufferedImage resizedImage = new BufferedImage(imageWidth, imageHeight, type);
        Graphics2D g = resizedImage.createGraphics();

        g.drawImage(originalImage, 0, 0, imageWidth, imageHeight, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    /**
     * Geeft de waarden imgBorderWidth, imgSizeX & imgSizeY welke door imageShow() gebruikt worden.
     */    //TODO dit veranderen
    public static Dimension setupImage(BufferedImage image, int centerPos, int size, int maxSizePercentage) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int newWidth, newHeight;
        int resize = size - centerPos;
        double resizeFactor;
        int maxscreenSize = (int) (screen.getHeight() * maxSizePercentage) / 100;

        if (image.getWidth() >= image.getHeight()) {//Landscape
            resizeFactor = (maxscreenSize - image.getWidth()) / centerPos;
            newWidth = (int) (image.getWidth() + resize * resizeFactor);
            float f = (float) newWidth / (float) image.getWidth();
            newHeight = (int) (image.getHeight() * f);
        } else {
            resizeFactor = (maxscreenSize - image.getHeight()) / centerPos;
            newHeight = (int) (image.getHeight() + resize * resizeFactor);
            float f = (float) newHeight / (float) image.getHeight();
            newWidth = (int) (image.getWidth() * f);
        }
        return new Dimension(newWidth, newHeight);
    }
}
