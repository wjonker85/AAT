import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: wjonker85
 * Date: 10/4/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
TODO grootte van de plaatjes dynamisch maken. Afhankelijk van de schermgrootte
 */
public class AATView extends PApplet implements Observer {
    private AATModel model;
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth, rB, gB, bB, stepStart, inputY;
    private int imgWidth, imgHeight, imgT;
    public float stepX, stepY, imgSizeX, imgSizeY, stepCount, imgRefactor, viewRatio, imgRatio, xPos, yPos;
    public PImage img;
    private boolean blackScreen = true;


    //Heb nu tijdelijk de grootte van de plaatjes toegevoegd aan de constructor. Deze waardes worden nu vanuit de main nog
    //doorgegeven. Is nu nog nodig omdat het model de plaatjes doorgeeft en het model ze niet zelf vanuit een bestand leest.
    public AATView(int viewWidth, int viewHeight,int stepSize) {
        // Tijdelijk, moet uit model komen.
        setVisible(true);

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;

        //Nodig vanwege het passend maken van een plaatje op het scherm
        this.imgWidth = 433;        //TODO veranderen zodat het dynamisch wordt
        this.imgHeight = 433;
        this.stepSize = stepSize;
    }

    public void setup() {
        size(viewWidth, viewHeight);
        stepStart = round((float) stepSize / 2f);
        inputY = stepStart;             //Eerste plaatje begint op stepStart.
        stepCount = stepStart;          //eerst stepCount begint op stepStart.
        borderWidth = 5;                //Breedte border om img
        imgBorderWidth = borderWidth;   //imgBorderWidth start met waarde borderWidth
        xPos = width / 2;               //Midden x coordinaar scherm
        yPos = height / 2;              //Midden y coordinaat scherm
    }

    public void draw() {
        setupImage();
        imageShow();
    }

    public void imageShow() {
        background(0);
        if (!blackScreen) {

            float[] colors = model.getBorderColor(imgT);
            rB = (int) colors[0];
            gB = (int) colors[1];
            bB = (int) colors[2];

            imageMode(CENTER);
            rectMode(CENTER);
            image(img, xPos, yPos, imgSizeX, imgSizeY);

            stroke(rB, gB, bB);
            strokeWeight(imgBorderWidth);
            fill(0, 0, 0, 0);
            rect(xPos, yPos, imgSizeX, imgSizeY);
        }
        fill(255, 0, 0);
        text("X: " + (int) imgSizeX + "px; Y: " + (int) imgSizeY + "px;" + " inputY: " + inputY, 5, 15);
    }

    //Wilfried, even tijdelijk jouw code uit de setup gehaald en in deze methode geplaatst. Vanwege null pointer exception, omdat de plaatjes
    //nu uit het model komen.
    private void setupImage() {
        //Ratio scherm en plaatje, nodig bij bepalen vergroot /verklein factor
        viewRatio = (float) viewHeight / (float) viewWidth;
        imgRatio = (float) imgHeight / (float) imgWidth;

        //Stapgroote bepale waarmee plaatjes verklijnt of vergoor moeten worden.
        stepX = (float) imgWidth / (float) stepSize;
        stepY = (float) imgHeight / (float) stepSize;

        //image size initialiseren wanneer deze voor het eerst op het scherm verschijnt
        imgSizeX = stepCount * stepX;
        imgSizeY = stepCount * stepY;

        //Vergroot / verklein factor van plaatje bepalen, zodat plaatje nooit groter kan worden dan het max hoogte of breedte van scherm
        if (viewRatio > imgRatio) {
            imgRefactor = (float) viewWidth / (float) imgWidth;
        } else {
            imgRefactor = (float) viewHeight / (float) imgHeight;
        }

        //resize the image
        imgBorderWidth = (int) (imgRefactor * borderWidth * inputY);
        imgSizeX = (imgRefactor * inputY * stepX);
        imgSizeY = (imgRefactor * inputY * stepY);
    }

    //Nodig om een BufferedImage om te zetten naar een PImage. Rechtstreeks geeft een exception
    private PImage convertImage(BufferedImage buf) {
        PImage img = new PImage(buf.getWidth(), buf.getHeight(), PConstants.ARGB);
        buf.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        return img;
    }

    /*
        Update ontvangt alle berichten van het Model (MVC pattern). Aan de hand van deze berichten wordt bepaald wat deze View
        moet doen. Plaatjes alleen laten zien wanneer dat toegestaan is. Deze plaatjes met een vaste factor telkens laten vergroten
        of verkleinen afhankelijk van de stand van de joystick.
    */
    public void update(Observable observable, Object o) {
        model = (AATModel) observable;

        //Informatie van de Y-as van de joystick. Om de grootte van het getoonde plaatje op aan te passen.
        if (o.toString().equals("Y-as")) {
            inputY = model.getPictureSize();
        }

        //Test heeft even een pauze. Mogelijkheid om een bericht te laten zien dat het pauze is.
        //Nu eerst alleen een zwart scherm
        if (o.toString().equals("Break")) {
            blackScreen = true;
            System.out.println("Test is on break");
        }

        //Plaatje is weggedrukt. Nu een zwart scherm laten zien.
        if (o.toString().equals("Wait screen")) {
            blackScreen = true;
        }

        //De test is gestart. Mogelijkheid om instructies te geven.
        //Nu eerst alleen een zwart scherm
        if (o.toString().equals("Start")) {
            System.out.println("Test started");
            blackScreen = true;

        }

        //Bericht uit het model dat het volgende plaatje getoond mag worden.
        if (o.toString().equals("Show Image")) {
            System.out.println("Show Picture");
            img = convertImage(model.getNextImage());
            imgT = model.getDirection();
            blackScreen = false;         //Plaatjes weer laten zien.
        }

        //Einde van de test.
        //Mogelijkheid tot het tonen van een bericht of mogelijk de optie om resultaten te laten zien.
        //Nu eerst alleen het scherm onzichtbaar maken.
        if (o.toString().equals("Test ended")) {
            System.out.println("Test ended");
            this.setVisible(false);
        }

    }
}

