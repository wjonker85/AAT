import processing.core.PApplet;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 29-10-11
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class AATViewResults extends PApplet implements Observer {
    private AATModel model;
    public int viewWidth, viewHeight;
    public float[] array1, array2, array3, array4;
    BoxPlot boxPlot;

    public AATViewResults(int viewWidth, int viewHeight) {
        // Tijdelijk, moet uit model komen.
        setVisible(true);

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        boxPlot = new BoxPlot(array1, array2, array3, array4);

    }

    public void setup(){
        size(viewHeight,viewWidth);
        noCursor();
    }

    public void draw(){
        /*drawBoxPlot(x, y, scalefactor, Y-as tekst, X-as tekst, conditie 1 label, conditie 2 label, conditie 3 label, conditie 4 label)
         bij scalefactor 1, is afmeting plaatje 500x500
          */
        boxPlot.drawBoxPlot(0, 0, 1f, "Reactie tijd (ms)", "Conditie", "Één", "Twee", "Drie", "Vier");
    }

    public void update(Observable observable, Object o) {
        model = (AATModel) observable;
    }
}