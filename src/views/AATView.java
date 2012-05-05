/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package views;

import AAT.Util.ImageUtils;
import DataStructures.AATImage;
import Model.AATModel;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

//import processing.core.PApplet;
//import processing.core.PConstants;
//import processing.core.PImage;

/**
 * Created by IntelliJ IDEA.
 * User: wjonker85
 * Date: 10/4/11
 * Time: 3:25 PM
 *
 */

/**
 * AATView is verantwoordelijk voor het tonen van test (Dat zijn de plaatjes, en instructie tekst). De resultaten
 * weergegeven door BoxPlot.java.
 */
//public class AATView extends PApplet implements Observer {
public class AATView extends JPanel implements Observer {
    /**
     * Variabelen en object aanmaken welke nodig zijn voor juiste werking AATView.java
     */
    private AATModel model;
    public int viewWidth, viewHeight, borderWidth, stepSize, imgBorderWidth, stepStart, inputY, xPos, yPos;
    public float stepCount;

    private boolean blackScreen = true;
    private boolean showInfo = true;
    private String displayText = "";
    private JEditorPane textPane;


    /**
     * @param model bevat o.a. waarden uit configuratie bestand
     */
    public AATView(AATModel model) {
        Dimension screen = this.getToolkit().getScreenSize();
        // int displayHeight = this.getToolkit().getScreenSize().height;
        //   Dimension screen = new Dimension(displayHeight,displayHeight);
        this.setLayout(null);
        this.setSize(screen);
        this.setPreferredSize(screen);
        this.setMinimumSize(screen);
        this.model = model;

        this.stepSize = model.getTest().getStepRate();
        this.setBackground(Color.black);
        textPane = new JEditorPane();

        textPane.setContentType("text/html");
        HTMLEditorKit kit = new HTMLEditorKit();
        textPane.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color: white; font-family:times; margin: 0px; background-color: #000;font : 30px monaco;}");
        styleSheet.addRule("p {color: white; font-family:times; margin: 0px; background-color: #000;font : 30px monaco;}");
        styleSheet.addRule("h1 {color: blue;font : 30px roman;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        Document doc = kit.createDefaultDocument();
        textPane.setDocument(doc);
        this.add(textPane);
        textPane.setBounds(100, 100, screen.width - 100, screen.height - 100);
        textPane.setEditable(false);
        this.setLayout(null);
        this.setVisible(true);
        stepStart = Math.round(stepSize / 2f);
        inputY = stepStart;                                 //Eerste plaatje begint op stepStart.
        stepCount = stepStart;                              //eerst stepCount begint op stepStart.
        borderWidth = model.getTest().getBorderWidth();               //Breedte border om img
        imgBorderWidth = borderWidth;                       //imgBorderWidth start met waarde borderWidth
        xPos = viewWidth / 2;
        yPos = viewHeight / 2;
        //Grote van de AATView scherm.
        this.viewHeight = this.getHeight();
        this.viewWidth = this.getWidth();
        // this.viewWidth = viewHeight; //make the display a square
        displayText = model.getTest().getIntroductionText(); //Test starts with an introduction tekst.


    }

    /**
     * De draw functie, omdat framerate(24) is wordt deze functie 24x per seconden doorlopen tijdens AAT
     */
    // public void draw() {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    // paints background
        if (!blackScreen) {
            textPane.setVisible(false);
            imageShow(g);                                    // Geef AAT weer
        } else if (showInfo) {
            infoShow(displayText);                          // Geef instructie tekst weer
        } else {
            textPane.setVisible(false);
            this.setBackground(new Color(0));
        }
    }


    /**
     * Functie welke tekst uit configuratie file op scherm toont, b.b. uitleg over test, of wanneer de gebruiker een
     * pauze kan houden.
     *
     * @param infoText bevat de waarde van displayText, en is de instructietekst welke op het scherm wordt getoond
     */
    private void infoShow(String infoText) {
        textPane.setVisible(true);
        this.setBackground(Color.black);   //Background to black
        this.setForeground(Color.white);    //ForeGround to white
        //    Font font = new Font("Roman", Font.PLAIN, 30);
        //    textPane.setFont(font);
        textPane.setBackground(new Color(0));
        //    textPane.setForeground(Color.white);
        textPane.setText(infoText);
        textPane.setEditable(false);
    }


    /**
     * imageShow is verantwoordelijk voor weergave plaatje en border van plaatje wanneer in config file ColoredBorders
     * True is. Deze functie bevat niet de resize functie, de juiste groote van het plaatje border en breedte van border
     * wordt berekend in setupImage();
     */
    public void imageShow(Graphics g) {
        this.setBackground(new Color(0));
        /**
         * setupImage geeft de juiste waarde voor imgSizeX imageSizeY zodat plaatje juiste afmeting heeft. Image()
         * is verantwoordelijk voor het daadwerkelijk laten zien van het plaatje.
         */
        BufferedImage img = model.getNextImage();
        int imgWidth = img.getWidth();
        //  imgHeight = img.height;
        int imgHeight = img.getHeight();
        Dimension imageSize = ImageUtils.setupImage(viewHeight, viewWidth, imgHeight, imgWidth, stepSize, (int) stepCount, inputY);
        /**
         * Wanneer in config file ColoredBorders True is, border weergeven, anders niet. De juiste waarden, zoals
         * breedte van border zijn al eerder met setupImage()
         *
         **/
        //TODO in andere class zetten
        if (inputY != stepStart) {    //  Only resize image when joystick moves
            img = ImageUtils.resizeImageWithHint(img, (int) imageSize.getWidth(), (int) imageSize.getHeight(), img.getType());
        }
        g.drawImage(img, getXpos(img), getYpos(img), this);
    }


    private int getXpos(BufferedImage image) {
        int XScreen = getWidth() / 2;
        int imageWidth = image.getWidth();
        return XScreen - imageWidth / 2;

    }

    private int getYpos(BufferedImage image) {
        int YScreen = getHeight() / 2;
        int imageHeight = image.getHeight();
        return YScreen - (imageHeight / 2);
    }

    /**
     * Update ontvangt alle berichten van het Model (MVC pattern). Aan de hand van deze berichten wordt bepaald wat
     * deze View moet doen. Plaatjes alleen laten zien wanneer dat toegestaan is. Deze plaatjes met een vaste factor
     * telkens laten vergroten of verkleinen afhankelijk van de stand van de joystick.
     *
     * @param observable Het model
     * @param o          message
     */
    public void update(Observable observable, Object o) {
        /**
         * Informatie van de Y-as van de joystick. Om de grootte van het getoonde plaatje op aan te passen.
         */
        if (o.toString().equals("Y-as")) {
            inputY = model.getPictureSize();
            repaint();
        }

        /**
         * Test heeft even een pauze. Mogelijkheid om een bericht te laten zien dat het pauze is. Nu eerst alleen een
         * zwart scherm
         */
        if (o.toString().equals("Break")) {
            displayText = model.getTest().getBreakText();
            blackScreen = true;
            showInfo = true;
            System.out.println("Test is on break");
            repaint();
        }

        /**
         * Plaatje is weggedrukt. Nu een zwart scherm laten zien.
         */
        if (o.toString().equals("Wait screen")) {
            inputY = model.getLastSize();
            repaint();
            if (model.getDirection() == AATImage.PULL) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            blackScreen = true;
            showInfo = false;
            repaint();
        }

        /**
         * Practice is geeindigd, getTestStartText() uit config file laden
         */
        if (o.toString().equals("Practice ended")) {
            displayText = model.getTest().getTestStartText();
            blackScreen = true;
            showInfo = true;
            repaint();
        }

        /**
         * Bericht uit het model dat het volgende plaatje getoond mag worden.
         */
        if (o.toString().equals("Show Image")) {
            blackScreen = false;         //Plaatjes weer laten zien.
            showInfo = false;
            repaint();
        }

        /**
         * Einde van de test. Mogelijkheid tot het tonen van een bericht of mogelijk de optie om resultaten te laten zien.
         * Nu eerst alleen het scherm onzichtbaar maken.
         * TODO: Scherm moet nog niet direct verdwijnen, eerst nog de tekst laten zien.
         */
        if (o.toString().equals("Show finished")) {
            displayText = model.getTest().getTestFinishedText();
            blackScreen = true;
            showInfo = true;
            repaint();
        }

    }
}

