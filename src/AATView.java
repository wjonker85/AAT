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
 * TODO: Betere methode van plaatjes laden.
 * TODO: Oplossing bedenken voor de image Setup. Misschien dat die waardes op een andere manier verkregen kunnen worden.
 * TODO: Zodat het laden van de plaatjes meer logisch wordt.
 * TODO: Zou het geen idee zijn om het formaat van het plaatje aan de schermgrootte aan te passen, ongeacht de grootte van het plaatje?
 * TODO: Bijv. gecentreerd op het scherm en altijd een bepaald percentage van de hoogte van scherm. zeg 50% bij het begin.
 * TODO: Nu heb je info van het plaatje nodig, nog voor het eerste plaatje geladen wordt. Je gaat er nu ook vanuit dat ieder plaatje
 * TODO: even groot zal zijn. Vast percentage is misschien logischer, omdat je dan alle plaatje even hoog of even breed maakt, ongeacht
 * TODO: de grootte van de plaatjes zoals ze opgeslagen zijn.
 */
public class AATView extends PApplet implements Observer {
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth, rB, gB, bB, stepStart, inputY, inputT;
    private int imgWidth, imgHeight;
    public float stepX, stepY, imgSizeX, imgSizeY, stepCount, imgRefactor, viewRatio, imgRatio, xPos, yPos;
    public PImage img;
    private boolean blackScreen = true;
    private AATModel model;

    //Heb nu tijdelijk de grootte van de plaatjes toegevoegd aan de constructor. Deze waardes worden nu vanuit de main nog
    //doorgegeven. Is nu nog nodig omdat het model de plaatjes doorgeeft en het model ze niet zelf vanuit een bestand leest.
    public AATView(int viewWidth, int viewHeight, int imgWidth, int imgHeight) {
        // Tijdelijk, moet uit model komen.
        setVisible(true);
        //Grote van de AATView scherm.

        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;

        //Nodig vanwege het passend maken van een plaatje op het scherm
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public void setup() {
        //Set size of window
        size(viewWidth, viewHeight);
        //   imageLoad();

        //Some var initialising
        stepSize = 9;
        stepStart = 5; //Verandert naar 5
        inputY = stepStart; //Eerste plaatje begint op stepStart.
        stepCount = stepStart;
        borderWidth = 10;
        imgBorderWidth = borderWidth;
        xPos = width / 2;
        yPos = height / 2;
        setupImage();

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
        imageRefactor();
    }

    public void draw() {
        imageRefactor();
        imageShow();
    }

    public void imageRefactor() {
        imgBorderWidth = (int) (imgRefactor * borderWidth * inputY);
        imgSizeX = (float) (imgRefactor * inputY * stepX);
        imgSizeY = (float) (imgRefactor * inputY * stepY);
    }

    public void imageShow() {
        background(0);
        if (!blackScreen) {
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

    /*   public void keyPressed() {
           if (key == 'n') {
                imageLoad();
            }
        }

    /*    public void imageLoad() {
            float imgT = random(0, 2);
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

         //   img = loadImage("images" + File.separator + ab + i + ".png");  //Zo zou het in Windows en Linux moeten werken


            imgSizeX = imgRefactor * stepCount * stepX;
            imgSizeY = imgRefactor * stepCount * stepY;
            imgBorderWidth = (int) (borderWidth * stepCount);
        }
    */

    //Update ontvangt alle berichten van het Model (MVC pattern)
    //TODO: nog wel wat verbeteren. Kan denk ik met wat minder berichten en de berichten mogen ook wel wat duidelijker.
    //TODO: plaatjes worden nu nog op teveel plekken geladen.
    //Volgorde is telkens black - image -black image etc.
    public void update(Observable observable, Object o) {
        model = (AATModel) observable;

        //Informatie van de Y-as van de joystick. Om de grootte van het getoonde plaatje op aan te passen.
        if (o.toString().equals("Y-as")) {
            inputY = model.getPictureSize();
            //    println("AATView model.getPictureSize(): " + model.getPictureSize());
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
            blackScreen = false;         //Plaatjes weer laten zien.
        }

        //Einde van de test.
        //Mogelijkheid tot het tonen van een bericht of mogelijk de optie om resultaten te laten zien.
        //Nu eerst alleen het scherm onzichtbaar maken.
        if (o.toString().equals("Test ended")) {
            System.out.println("Test ended");
            this.setVisible(false);
        }


        //Informatie of deze view wel of niet zichtbaar hoort te zijn.
        if (o.equals("View changed")) {
            if (model.getCurrentView() == AATModel.TEST_VIEW) {
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }
        }
    }


    //Nodig om een BufferedImage om te zetten naar een PImage. Rechtstreeks geeft een exception
    private PImage convertImage(BufferedImage buf) {
        PImage img = new PImage(buf.getWidth(), buf.getHeight(), PConstants.ARGB);
        buf.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        return img;
    }
}

