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

import Model.AATModel;
import views.Questionnaire.DisplayQuestionnairePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 11:26 AM
 * JFrame that contains the test view. An extra frame is needed so that the Processing object can be displayed.
 * This frame lets the test be executed full screen.   TODO cardlayout gebruiker
 */
public class TestFrame extends JFrame implements Observer {

    //  private AATView aatView;
    private AATModel model;
    private CardLayout cl;
    private JPanel displayPanel;
    private BoxPlot boxPlot;
    private PlotPanel plotPanel;
    private AATView aatView;
    private DisplayQuestionnairePanel questionsView;
    private Cursor invisibleCursor;

    //Make it a fullscreen frame
    public TestFrame(AATModel model) {
        this.model = model;
        cl = new CardLayout();
        displayPanel = new JPanel(cl);
        Dimension screen = getToolkit().getScreenSize();
        questionsView = new DisplayQuestionnairePanel(model,new Dimension((int) (0.8 * screen.width), (int) (0.75 * screen.height)));
        aatView = new AATView(model);
        boxPlot = new BoxPlot(model);
        plotPanel = new PlotPanel(model);
        model.addObserver(aatView);
        displayPanel.add(questionsView, "questions");
        displayPanel.add(aatView, "aat");
        displayPanel.add(boxPlot, "boxPlot");
        displayPanel.add(plotPanel, "plot");
        //  resultsView = new BoxPlot()

        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.getContentPane().setBackground(Color.black);
        //   this.getContentPane().add(qPanel, new GridBagConstraints());
        //   this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(displayPanel, new GridBagConstraints());
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setEnabled(true);
        setVisible(true);

        //Create a transparant cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(0, 0);
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke,
                "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

    }

    private void closeWindow() {
        model.deleteObservers();
        aatView = null;
        boxPlot = null;
        questionsView = null;
        plotPanel = null;
        System.gc();
        this.setVisible(false);
        this.setEnabled(false);
        this.dispose();
        //   this.setVisible(false);
        //  this.setEnabled(false);
        //  model.clearAll();
    }

    /*
       The model determines which screen needs to be active. The questions screen, the test screen of the results screen
       It does this by sending update messages to it's Observers, like this class is an observer of the model.
     */
    public void update(Observable observable, Object o) {

        //AAT Test screen
        if (o.toString().equals("Start")) {
            setCursor(invisibleCursor);
            cl.show(displayPanel, "aat");
        }

        //Questions Screen
        if (o.toString().equals("Show questions")) {
            setCursor(Cursor.getDefaultCursor());
            questionsView.displayQuestions(model.getTest().getQuestionnaire());
            cl.show(displayPanel, "questions");
        }

        //Results Screen
        if (o.toString().equals("Display results")) {
            if (model.getTest().getTestConfiguration().getPlotType().equalsIgnoreCase("boxplot")) {
                boxPlot.init();
                try {
                    boxPlot.display(true);
                    cl.show(displayPanel, "boxPlot");
                } catch (Exception e) {
                    System.out.println("Error: Boxplot cannot be shown");
                }
            }
            else {
                setCursor(Cursor.getDefaultCursor());
                plotPanel.displayPlot(true);
                cl.show(displayPanel,"plot");
            }
        }

        if (o.toString().equals("Finished")) {
            boxPlot = null;
            System.gc();
            boxPlot = new BoxPlot(model);
            plotPanel = new PlotPanel(model);
            this.setVisible(false);
        }
    }
}
