import processing.core.PApplet;
import processing.core.PImage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: wjonker85
 * Date: 10/4/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AATView extends PApplet implements Observer {
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth;
    public float stepX, stepY, imgSizeX, imgSizeY, stepCount, imgRefactor, viewRatio, imgRatio, xPos, yPos;
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
        //Set size of window
        size(viewWidth, viewHeight);

        //Some var initialising
        img = loadImage("D:\\Documenten\\Prive\\Familie\\Foto's\\Barcalona\\IMG_3505.JPG");
        stepSize = 7;
        stepCount = 4;
        borderWidth = 8;
        imgBorderWidth = borderWidth;
        xPos = width / 2;
        yPos = height / 2;

        //Ratio scherm en plaatje, nodig bij bepalen vergroot /verklein factor
        viewRatio = (float) viewHeight / (float) viewWidth;
        imgRatio = (float) img.height / (float) img.width;

        //Stapgroote bepale waarmee plaatjes verklijnt of vergoor moeten worden.
        stepX = (float) img.width / (float) stepSize;
        stepY = (float) img.height / (float) stepSize;

        //image size initialiseren wanneer deze voor het eerst op het scherm verschijnt
        imgSizeX = stepCount * stepX;
        imgSizeY = stepCount * stepY;

        //Vergroot / verklein factor van plaatje bepalen, zodat plaatje nooit groter kan worden dan het max hoogte of breedte van scherm
        if (viewRatio > imgRatio) {
            imgRefactor = (float) viewWidth / (float) img.width;
        } else {
            imgRefactor = (float) viewHeight / (float) img.height;
        }
    }

    public void draw() {
        int time = millis();
        background(0);
        imageMode(CENTER);
        rectMode(CENTER);
        image(img, xPos, yPos, imgSizeX, imgSizeY);
        stroke(75, 100, 255);
        strokeWeight(imgBorderWidth);
        fill(0, 0, 0, 0);
        rect(xPos, yPos, imgSizeX, imgSizeY);
        fill(255);
        text(time, width - 70, height - 10);
    }

    public void keyPressed() {
        if (keyCode == 38 && stepCount < stepSize) {
            stepCount++;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (keyCode == 38 && stepCount >= stepSize) {
            stepCount = stepSize;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (keyCode == 40 && stepCount > 0) {
            stepCount--;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (keyCode == 40 && stepCount <= 0) {
            stepCount = 0;
            imgBorderWidth = 0;
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        }
        println("x: " + imgSizeX);
        println("y: " + imgSizeY);
    }


    public void mouseMoved() {
        if (pmouseY < mouseY && stepCount < stepSize) {
            stepCount++;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (pmouseY == mouseY && stepCount >= stepSize) {
            stepCount = stepSize;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (pmouseY > mouseY && stepCount > 0) {
            stepCount--;
            imgBorderWidth = (int) (borderWidth * stepCount);
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        } else if (pmouseY > mouseY && stepCount <= 0) {
            stepCount = 0;
            imgBorderWidth = 0;
            imgSizeX = (float) (imgRefactor * (stepCount * stepX));
            imgSizeY = (float) (imgRefactor * (stepCount * stepY));
        }
        println("x: " + imgSizeX);
        println("y: " + imgSizeY);
        println("r: " + imgRefactor);

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
