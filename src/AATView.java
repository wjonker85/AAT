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
    public int viewWidth, viewHeight, imgSize, maxSize, borderWidth;
    public PImage img;
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
        imgSize = resizeStep() * 3; // set initial size for image;
        //smooth(); // no smooth, makes it to slow, processing opengl lib gives problems
        maxSize = resizeStep() * 7;
        borderWidth = 20;
    }

    public void draw() {
        background(0);
        imageMode(CENTER);
        rectMode(CENTER);
        translate(width / 2, height / 2);
        stroke(0, 255, 0);
        strokeWeight(borderWidth);
        rect(0, 0, imgSize, imgSize);
        image(img, 0, 0, imgSize, imgSize);

    }

    //Grote van resize factor is gebonden aan scherm groote, zodat er max 7 stappen zijn.
    public int resizeStep() {
        int i;
        if (viewHeight > viewWidth) {
            i = (int) viewWidth / 7;
        } else {
            i = (int) viewHeight / 7;
        }
        return i;
    }

    public void keyPressed() {
        if (keyCode == 38) {
            if (imgSize < maxSize) {
                imgSize += resizeStep();
            }
        } else if (keyCode == 40) {
            if (imgSize < resizeStep()) {
                imgSize = 0;
            } else
                imgSize -= resizeStep();
        }

        //Some debug code
        println("resizeStep: " + resizeStep());
        println("Size: " + imgSize);
        println("maxSize: " + maxSize);
    }

    public void mouseMoved() {
        if (mouseY > pmouseY) {
            if (imgSize < maxSize) {
                imgSize += resizeStep();
            }
        } else if (mouseY < pmouseY) {
            if (imgSize < resizeStep()) {
                imgSize = 0;
            } else
                imgSize -= resizeStep();
        }
        println(mouseY);
    }

    //Wanneer TEST_VIEW true is word setvisible op true zodat AATView view getoond word op scherm
    //Ook een update gemaakt voor als de y-as van de joystick verandert. Doet nu alleen een println met de int waardes tussen -3 en 3
    //Methode nog veranderen zodat Object o altijd een integer waarde wordt.
    public void update(Observable observable, Object o) {
        model = (AATModel) observable;

        if (o.toString().equals("Y-as")) {
            System.out.println("Resize "+model.getPictureSize());
        }
        if (o.equals("View changed")) {
            if (model.getCurrentView() == AATModel.TEST_VIEW) {
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }
        }

    }
}
