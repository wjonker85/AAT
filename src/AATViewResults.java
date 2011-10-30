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
        size(200,200);
    }

    public void draw(){
        rectMode(CORNERS);
        rect(0,0, 200, 200);
        boxPlot.drawBoxPlot(0, 0, 1.5f, "Reactie tijd (ms)", "Conditie", "Één", "Twee", "Drie", "Vier");
    }





    public void update(Observable observable, Object o) {
        model = (AATModel) observable;
    }
}