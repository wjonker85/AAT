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
public class AATView extends JPanel implements Observer {
    /**
     * Variabelen en object aanmaken welke nodig zijn voor juiste werking AATView.java
     */
    private AATModel model;

    private int inputY;
    private int centerPoint;


    private boolean blackScreen = true;
    private boolean showInfo = true;
    private String displayText = "";
    private JEditorPane textPane;
    private JScrollPane scrollPane;
    private Dimension screen;

    /**
     * @param model bevat o.a. waarden uit configuratie bestand
     */
    public AATView(AATModel model) {
        screen = this.getToolkit().getScreenSize();
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(screen);
        this.setMinimumSize(screen);
        this.model = model;
        this.setBackground(new java.awt.Color(0, 0, 0, 0));
        this.setBorder(null);

        textPane = new JEditorPane();
        scrollPane = new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension((int)(0.8 * screen.width),(int)(0.7 *screen.height)));
        this.add(scrollPane);
        scrollPane.setOpaque(false);
        textPane.setEditable(false);
        scrollPane.setBorder(null);
        textPane.setBorder(null);
        centerPoint = Math.round(model.getTest().getTestConfiguration().getStepSize() / 2);      //eerst centerPoint begint op stepStart.
        inputY = centerPoint; //Start in the center
        displayText = model.getTest().getTranslation("introduction"); //Test starts with an introduction tekst.
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    // paints background

        if (!blackScreen) {
            scrollPane.setVisible(false);
            imageShow(g);                                    // Geef AAT weer
        } else {
            if (showInfo) {
                textPane.setContentType("text/html");
                textPane.setBackground(new java.awt.Color(0, 0, 0, 0));
                HTMLEditorKit kit = new HTMLEditorKit();
                textPane.setEditorKit(kit);
                StyleSheet styleSheet = kit.getStyleSheet();
                styleSheet.addRule("body {color: white; font-family:times; margin: 0px; background-color: black;font : 24px monaco;}");
                styleSheet.addRule("h2 {color: white; font-family:times; margin: 0px; background-color: black;font : 24px monaco;}");
                styleSheet.addRule("h1 {color:white;background-color: black}");
                styleSheet.addRule("h3 {color:white; background-color: black}");
                styleSheet.addRule("p {color:white;background-color: black}");
                Document doc = kit.createDefaultDocument();
                textPane.setDocument(doc);
                infoShow(displayText);                          // Geef instructie tekst weer
            } else {
                scrollPane.setVisible(false);
                this.setBackground(new Color(0));
            }
        }
    }


    /**
     * Functie welke tekst uit configuratie file op scherm toont, b.b. uitleg over test, of wanneer de gebruiker een
     * pauze kan houden.
     *
     * @param infoText bevat de waarde van displayText, en is de instructietekst welke op het scherm wordt getoond
     */
    private void infoShow(String infoText) {
        scrollPane.setVisible(true);
        this.setBackground(Color.black);   //Background to black
        this.setForeground(Color.white);    //ForeGround to white
        textPane.setBackground(new Color(0));
        if (!infoText.contains("<body>")) {
            if(!infoText.contains("<") && !infoText.contains(">")) {
                infoText = infoText.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");            //probably no html content at al, so replace newline with <br>
            }
            infoText = "<body><h2>" + infoText + "</h2></body>";
        }
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
        BufferedImage img = model.getNextImage();
        Dimension imageSize = ImageUtils.setupImage(img, centerPoint, inputY, model.getTest().getTestConfiguration().getMaxSizePerc());
        if (inputY != centerPoint) {    //  Only resize image when joystick moves
            img = ImageUtils.resizeImageWithHint(img, (int) imageSize.getWidth(), (int) imageSize.getHeight(), img.getType());
        }
        g.drawImage(img, getXpos(img), getYpos(img), this);
    }


    private int getXpos(BufferedImage image) {
        int XScreen = screen.width / 2;
        int imageWidth = image.getWidth();
        return XScreen - imageWidth / 2;
    }

    private int getYpos(BufferedImage image) {
        int YScreen = screen.height / 2;
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
            return;
        }

        /**
         * Test heeft even een pauze. Mogelijkheid om een bericht te laten zien dat het pauze is. Nu eerst alleen een
         * zwart scherm
         */
        if (o.toString().equals("Break")) {
            displayText = model.getTest().getTranslation("break");
            blackScreen = true;
            showInfo = true;
            repaint();
            return;
        }

        /**
         * Plaatje is weggedrukt. Nu een zwart scherm laten zien.
         */
        if (o.toString().equals("Wait screen")) {
            inputY = model.getLastSize();
            if (model.getDirection() == AATImage.PULL) {
                inputY--;
            }
            repaint();
            if (model.getDirection() == AATImage.PULL) {
                try {
                    Thread.sleep(80);
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
            return;
        }

        /**
         * Practice is geeindigd, getTestStartText() uit config file laden
         */
        if (o.toString().equals("Practice ended")) {
            displayText = model.getTest().getTranslation("start");
            blackScreen = true;
            showInfo = true;
            repaint();
            return;
        }

        /**
         * Bericht uit het model dat het volgende plaatje getoond mag worden.
         */
        if (o.toString().equals("Show Image")) {
            inputY = centerPoint;
            blackScreen = false;         //Plaatjes weer laten zien.
            showInfo = false;
            repaint();
            return;
        }

        /**
         * Einde van de test. Mogelijkheid tot het tonen van een bericht of mogelijk de optie om resultaten te laten zien.
         * Nu eerst alleen het scherm onzichtbaar maken.
         */
        if (o.toString().equals("Show finished")) {
            displayText = model.getTest().getTranslation("finished");
            blackScreen = true;
            showInfo = true;
            repaint();
        }
    }
}

