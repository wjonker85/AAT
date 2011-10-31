import processing.core.PApplet;

import javax.swing.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 29-10-11
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class AATViewResults extends JPanel {
    private AATModel model;
    public int viewWidth, viewHeight;
    private String[] labelsArray;
    public float[] array1, array2, array3, array4;
    BoxPlot boxPlot;

    public AATViewResults(int viewWidth, int viewHeight, AATModel model) {
        // Tijdelijk, moet uit model komen.
     //   setVisible(true);

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        this.setSize(viewWidth,viewHeight);
        //        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        test(array4);
   //     System.out.println("Array Size "+array1.length);
        boxPlot = new BoxPlot(array1, array2, array3, array4,labelsArray, viewWidth,viewHeight);
        boxPlot.init();
        this.add(boxPlot);
//        boxPlot.drawBoxPlot(0, 0, 1f, "Reactie tijd (ms)", "Conditie", labelsArray[0], labelsArray[1], labelsArray[2], labelsArray[3]);

    }

 //   public void setup() {
  //      size(viewHeight, viewWidth);
     //   noCursor();
  //  }

    private void test(float[] data) {
        for(int x =0;x<data.length;x++) {
    //        System.out.println("ReactionTime "+data[x]);
        }
    }

    public void draw() {
        /*drawBoxPlot(x, y, scalefactor, Y-as tekst, X-as tekst, conditie 1 label, conditie 2 label, conditie 3 label, conditie 4 label)
         bij scalefactor 1, is afmeting plaatje 500x500
          */

    }
}