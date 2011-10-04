import processing.core.PApplet;
import processing.core.PImage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AATView extends PApplet implements Observer {
    int viewWidth, viewHeight;
    int i;
    PImage img;

    private AATModel model;

    public AATView(int viewWidth, int viewHeight) {
        // Tijdelijk, moet uit model komen.
        setVisible(true);

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
    }

    public void setup() {
        size(viewWidth, viewHeight);
        img = loadImage("C:\\Users\\Public\\Pictures\\Sample Pictures\\Penguins800_800.jpg");
        smooth();
    }

    public void draw() {
        background(0);
        imageMode(CENTER);
        rectMode(CENTER);
        translate(width / 2, height / 2);
        stroke(0, 255, 0);
        strokeWeight(20);
        rect(0, 0, i, i);
        image(img, 0, 0, i, i);

        if (keyPressed == true) {
            if (keyCode == 38) {
                i += 30;
            } else if (keyCode == 40) {
                i -= 30;
            }
        }

    }

    public void display() {
    }

    //Wanneer TEST_VIEW true is word setvisible op true zodat AATView view getoond word op scherm
    public void update(Observable observable, Object o) {
        model = (AATModel) observable;
        if (o.equals("View changed")) {
            if (model.currentView() == AATModel.TEST_VIEW) {
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }
        }

    }
}
