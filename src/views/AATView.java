package views;

import Model.AATModel;
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
 */

/**
 * AATView is verantwoordelijk voor het tonen van test (Dat zijn de plaatjes, en instructie tekst). De resultaten
 * weergegeven door BoxPlot.java.
 */
public class AATView extends PApplet implements Observer {

    /**
     * Variabelen en object aanmaken welke nodig zijn voor juiste werking AATView.java
     */
    private AATModel model;
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth, stepStart, inputY;
    private int imgWidth, imgHeight, imgT;
    public float stepX, stepY, imgSizeX, imgSizeY, stepCount, imgRefactor, viewRatio, imgRatio, xPos, yPos;
    public PImage img;
    private boolean blackScreen = true;
    private boolean showInfo = true;
    private String displayText = "";


    /**
     * @param model      bevat o.a. waarden uit configuratie bestand
     * @param viewHeight is de hoogte van het scherm in pixxels
     * @param viewWidth  is breedte van scherm in pixxels
     */
    public AATView(AATModel model, int viewHeight, int viewWidth) {
        System.out.println("New AAT View started");
        this.model = model;

        //Grote van de AATView scherm.
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;

        //Hoeveel stappen heeft joystick
        this.stepSize = model.getStepRate();
        displayText = model.getIntroductionText(); //Test starts with an introduction tekst.

    }


    /**
     * Wordt geladen wanneer AATView voor het eerst wordt opgeroep
     */
    public void setup() {
        size(viewWidth, viewHeight);
        stepStart = round((float) stepSize / 2f);
        inputY = stepStart;                                 //Eerste plaatje begint op stepStart.
        stepCount = stepStart;                              //eerst stepCount begint op stepStart.
        borderWidth = model.getBorderWidth();               //Breedte border om img
        imgBorderWidth = borderWidth;                       //imgBorderWidth start met waarde borderWidth
        xPos = width / 2;                                   //Midden x coordinaar scherm
        yPos = height / 2;                                  //Midden y coordinaat scherm
        noCursor();                                         //Geen cursor weergeven tijdens AAT
        frameRate(24);                                      //Standaard framerate is 60, dit is echter hoger dan nodig
    }


    /**
     * De draw functie, omdat framerate(24) is wordt deze functie 24x per seconden doorlopen tijdens AAT
     */
    public void draw() {
        if (!blackScreen) {
            imageShow();                                    // Geef AAT weer
        } else if (showInfo) {
            infoShow(displayText);                          // Geef instructie tekst weer
        } else {
            background(0);
        }
    }


    /**
     * Functie welke tekst uit configuratie file op scherm toont, b.b. uitleg over test, of wanneer de gebruiker een
     * pauze kan houden.
     *
     * @param infoText bevat de waarde van displayText, en is de instructietekst welke op het scherm wordt getoond
     */
    private void infoShow(String infoText) {
        background(0);
        fill(255);
        textSize(30);
        textAlign(CENTER);

        /**
         * Scalefactor bevat de scale waarde zodat tekst in de breedt altijd beeld vullende is, met hoogte wordt geen
         * rekening gehouden omdat in alle gangbare resoluties een hogere breedte dan hoogte hebben
         * textWidth(infoText)+50f is omdat soms scale factor net te groot is waardoor text precies op rand of een paar
         * pixels buiten rand valt
         */
        float scaleFactorText = ((float) width / (textWidth(infoText) + 50f));
        scale(scaleFactorText);

        /**
         * infotext is tekst die getoond moet worden, Xpos is midden, maar door de scale factor zal is Xpos niet meer
         * het visuele middden van scherm, daarom (xPos / scaleFactorText). In hoogte moet tekst vanaf 1/4 scherm komen,
         * vandaar viewHeig * .25, maar ook hier moet weer rekening gehouden worden met scalefactor, daarom
         * ((float)viewHeight *.25f) / scaleFactorText.
         */
        text(infoText, (xPos / scaleFactorText), ((float) viewHeight * .25f) / scaleFactorText);
    }


    /**
     * Geeft de waarden imgBorderWidth, imgSizeX & imgSizeY welke door imageShow() gebruikt worden.
     */
    private void setupImage() {
        /**
         * Ratio scherm en plaatje, nodig bij bepalen vergroot /verklein factor.
         */
        viewRatio = (float) viewHeight / (float) viewWidth;
        imgRatio = (float) imgHeight / (float) imgWidth;

        /**
         * Stapgroote bepale waarmee plaatjes verklijnt of vergoor moeten worden.
         */
        stepX = (float) imgWidth / (float) stepSize;
        stepY = (float) imgHeight / (float) stepSize;

        /**
         * image size initialiseren wanneer deze voor het eerst op het scherm verschijnt.
         */
        imgSizeX = stepCount * stepX;
        imgSizeY = stepCount * stepY;

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
        imgBorderWidth = (int) (imgRefactor * borderWidth * inputY);
        imgSizeX = (imgRefactor * inputY * stepX);
        imgSizeY = (imgRefactor * inputY * stepY);
    }


    /**
     * imageShow is verantwoordelijk voor weergave plaatje en border van plaatje wanneer in config file ColoredBorders
     * True is. Deze functie bevat niet de resize functie, de juiste groote van het plaatje border en breedte van border
     * wordt berekend in setupImage();
     */
    public void imageShow() {
        background(0);

        /**
         * setupImage geeft de juiste waarde voor imgSizeX imageSizeY zodat plaatje juiste afmeting heeft. Image()
         * is verantwoordelijk voor het daadwerkelijk laten zien van het plaatje.
         */
        setupImage();
        imageMode(CENTER);
        image(img, xPos, yPos, imgSizeX, imgSizeY);

        /**
         * Wanneer in config file ColoredBorders True is, border weergeven, anders niet. De juiste waarden, zoals
         * breedte van border zijn al eerder met setupImage()
         *
         * stroke => laad de argb waarde uit model halen, dit is de kleur van de border
         * StrokeWeight => bepaald breedte van border op basis van BorderWidth X in config file uit model halen
         * fill => is transparant, zodat plaatje zichtbaar is :) (Wel zo handig!!);
         * Rect => Border met juist kleur en afmeting weergeven imgSizeX & imgSizeY zijn bekend omdat deze zijn berekend
         * door setupImage() welke eerder in dit object is opgeroepen
         */
        if (model.hasColoredBorders()) {
            rectMode(CENTER);
            stroke(unhex(model.getBorderColor(imgT)));
            strokeWeight(imgBorderWidth);
            fill(0, 0, 0, 0);
            rect(xPos, yPos, imgSizeX, imgSizeY);
        }
    }


    /**
     * Nodig om een BufferedImage om te zetten naar een PImage. Rechtstreeks geeft een exception
     *
     * @param buf krijgt de waarde model.getNextImage() uit het model, en bevcat het plaatje.
     * @return geeft de PImage terug, welke door prcoessing gebruikt kan worden.
     */
    private PImage convertImage(BufferedImage buf) {
        PImage img = new PImage(buf.getWidth(), buf.getHeight(), PConstants.ARGB);
        buf.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        return img;
    }


    /**
     * Update ontvangt alle berichten van het Model (MVC pattern). Aan de hand van deze berichten wordt bepaald wat
     * deze View moet doen. Plaatjes alleen laten zien wanneer dat toegestaan is. Deze plaatjes met een vaste factor
     * telkens laten vergroten of verkleinen afhankelijk van de stand van de joystick.
     *
     * @param observable
     * @param o
     */
    public void update(Observable observable, Object o) {
        /**
         * Informatie van de Y-as van de joystick. Om de grootte van het getoonde plaatje op aan te passen.
         */
        if (o.toString().equals("Y-as")) {
            inputY = model.getPictureSize();
        }

        /**
         * Test heeft even een pauze. Mogelijkheid om een bericht te laten zien dat het pauze is. Nu eerst alleen een
         * zwart scherm
         */
        if (o.toString().equals("Break")) {
            displayText = model.getBreakText();
            blackScreen = true;
            showInfo = true;
            System.out.println("Test is on break");
        }

        /**
         * Plaatje is weggedrukt. Nu een zwart scherm laten zien.
         */
        if (o.toString().equals("Wait screen")) {
            blackScreen = true;
            showInfo = false;
        }

        /**
         * Practice is geindigd, getTestStartText() uit config file laden
         */
        if (o.toString().equals("Practice ended")) {
            System.out.println("End of practice");
            displayText = model.getTestStartText();
            blackScreen = true;
            showInfo = true;
        }

        /**
         * Bericht uit het model dat het volgende plaatje getoond mag worden.
         */
        if (o.toString().equals("Show Image")) {
            System.out.println("Show Picture");
            img = convertImage(model.getNextImage());
            System.out.println("imgWidth " + img.width + " " + img.height);
            imgWidth = img.width;

            imgHeight = img.height;
            imgT = model.getDirection();
            blackScreen = false;         //Plaatjes weer laten zien.
            showInfo = false;
        }

        /**
         * Einde van de test. Mogelijkheid tot het tonen van een bericht of mogelijk de optie om resultaten te laten zien.
         * Nu eerst alleen het scherm onzichtbaar maken.
         * TODO: Scherm moet nog niet direct verdwijnen, eerst nog de tekst laten zien.
         */
        if (o.toString().equals("Show finished")) {
            displayText = model.getTestFinishedText();
            blackScreen = true;
            showInfo = true;
            //    this.setVisible(false);
        }

    //    if (o.toString().equals("Display results")) {
     //       this.setVisible(false);
     //       this.setEnabled(false);
     //   }
    }
}

