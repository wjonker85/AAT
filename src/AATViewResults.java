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

    public AATViewResults(int viewWidth, int viewHeight) {
        // Tijdelijk, moet uit model komen.
        setVisible(true);

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;

    }
}