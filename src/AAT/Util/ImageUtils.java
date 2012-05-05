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
 * To change this template use File | Settings | File Templates.
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
     */
    public static Dimension setupImage(int viewHeight, int viewWidth, int imgHeight, int imgWidth, int stepSize, int stepCount, int size) {
        /**
         * Ratio scherm en plaatje, nodig bij bepalen vergroot /verklein factor.
         */
        float imgRefactor;
        float viewRatio = (float) viewHeight / (float) viewWidth;
        float imgRatio = (float) imgHeight / (float) imgWidth;

        /**
         * Stapgroote bepale waarmee plaatjes verklijnt of vergoor moeten worden.
         */
        float stepX = (float) imgWidth / (float) stepSize;
        float stepY = (float) imgHeight / (float) stepSize;

        /**
         * image size initialiseren wanneer deze voor het eerst op het scherm verschijnt.
         */
        float imgSizeX = stepCount * stepX;
        float imgSizeY = stepCount * stepY;
        /**
         * Vergroot / verklein factor van plaatje bepalen, zodat plaatje nooit groter kan worden dan het max hoogte of breedte van scherm
         */
        if (viewRatio > imgRatio) {
            imgRefactor = (float) viewWidth / (float) imgWidth;
        } else {
            imgRefactor = (float) viewHeight / (float) imgHeight;
        }

        /**
         * De daadwerkelijke breedt, hoogte en boorderwidth in variabelen stoppen.
         */
        //    float imgBorderWidth = (borderWidth * inputY);
        imgSizeX = (imgRefactor * size * stepX);
        imgSizeY = (imgRefactor * size * stepY);
        Dimension d = new Dimension((int) imgSizeX, (int) imgSizeY);
        return d;
    }

    public static Dimension setupImage(int viewHeight, int viewWidth, int imgHeight, int imgWidth, int stepSize) {
        int stepStart = Math.round(stepSize / 2f);
        return setupImage(viewHeight, viewWidth, imgHeight, imgWidth, stepSize, stepStart, stepStart);
    }
}
