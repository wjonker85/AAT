import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: wjonker85
 * Date: 10/4/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 * TODO: Betere methode van plaatjes laden.
 */
public class AATView extends PApplet implements Observer {
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth, rB, gB, bB, stepStart, inputY, inputT;
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
        imageLoad();

        //Some var initialising
        stepSize = 7;
        stepStart = 4; //Verandert van 4 naar 7
        stepCount = stepStart;
        borderWidth = 10;
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

        imageRefactor();
    }

    public void draw() {
        imageRefactor();
        imageShow();
    }

    public void imageRefactor() {
        imgBorderWidth = (int) (imgRefactor * borderWidth * inputY);
        imgSizeX = imgRefactor * inputY * stepX;
        imgSizeY = imgRefactor * inputY * stepY;
    }

    public void imageShow() {
        background(0);
        imageMode(CENTER);
        rectMode(CENTER);
        image(img, xPos, yPos, imgSizeX, imgSizeY);
        stroke(rB, gB, bB);
        strokeWeight(imgBorderWidth);
        fill(0, 0, 0, 0);
        rect(xPos, yPos, imgSizeX, imgSizeY);
        fill(255, 0, 0);
        text("n = imageLoad()", 5, 15);
        text("X: "+ (int)imgSizeX + " Y: "+ (int)imgSizeY, 5, 30);
    }

    //Waarde Y uit model overzetten naar inputY
    public void deviceUpdateY(int inputY) {
        this.inputY = inputY;
    }

    //Waarde tricker uit model overzetten naar inputT
    public void deviceUpdateT(int inputT) {
        this.inputT = inputT;
    }

    public void keyPressed(){
        if(key == 'n'){
            imageLoad();
        }
    }

    public void imageLoad() {
        float imgT = random(0,2);
        int i;
        char ab;
        i = (int) random(1, 6);

        if ((int) imgT == 0) {
            ab = 'a';
            rB = 245;
            gB = 254;
            bB = 2;
        } else {
            ab = 'b';
            rB = 0;
            gB = 164;
            bB = 231;
        }

        img = loadImage("images" + File.separator + ab + i + ".png");  //Zo zou het in Windows en Linux moeten werken
        imgSizeX = imgRefactor * stepCount * stepX;
        imgSizeY = imgRefactor * stepCount * stepY;
        imgBorderWidth = (int) (borderWidth * stepCount);
    }



    //Wanneer TEST_VIEW true is word setvisible op true zodat AATView view getoond word op scherm
    public void update(Observable observable, Object o) {
        model = (AATModel) observable;

        if (o.toString().equals("Y-as")) {
            deviceUpdateY(model.getPictureSize());
        }

        if (o.toString().equals("Trigger")) {
            //deviceUpdateT(model.getTrigger); TODO: in AATView trigger initialiseren
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

