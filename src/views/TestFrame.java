package views;

import Model.AATModel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 11:26 AM
 * JFrame that contains the test view. An extra frame is needed so that the Processing object can be displayed.
 * This frame lets the test be executed full screen.
 */
public class TestFrame extends JFrame implements Observer {

    private AATView aatView;
    private AATModel model;
    private QuestionPanel qPanel;

    //Make it a fullscreen frame
    public TestFrame(AATModel model) {
        this.model = model;
        //  this.setLayout(new GridBagLayout());
        this.getContentPane().setLayout(new GridBagLayout());
        //   if(model.getExtraQuestions().size()>0) {
        qPanel = new QuestionPanel(model);

        //   }
        this.setBackground(Color.black);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setEnabled(true);
        setVisible(false);
    }


    /*
       The model determines which screen needs to be active. The questions screen, the test screen of the results screen
       It does this by sending update messages to it's Observers, like this class is an observer of the model.
     */
    public void update(Observable observable, Object o) {

        //AAT Test screen
        if (o.toString().equals("Start")) {
            this.setVisible(true);
            this.setLayout(null);

            if (aatView != null) {
                model.deleteObserver(aatView);
            }
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) dim.getWidth();
            int screenHeight = (int) dim.getHeight();
            AATView aatView = new AATView(model, screenHeight, screenWidth);
            aatView.init();
            model.addObserver(aatView);
            this.getContentPane().add(aatView);
        }


        //Questions Screen
        if (o.toString().equals("Show questions")) {
            this.setVisible(true);
            //    System.out.println("Show questions");
            qPanel = new QuestionPanel(model);
            this.getContentPane().setBackground(Color.black);
            this.getContentPane().add(qPanel, new GridBagConstraints());
            qPanel.displayQuestions(model.getExtraQuestions());
        }

        //Results Screen
        if (o.toString().equals("Display results")) {
            this.getContentPane().removeAll();
            this.getContentPane().setBackground(Color.black);
            this.setBackground(Color.black);
            model.deleteObserver(aatView);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) dim.getWidth();
            int screenHeight = (int) dim.getHeight();
            this.setLayout(null);
            Set labels = model.getResultsPerCondition().keySet();        //Get results from the model
            String[] labelsArray = Arrays.copyOf(labels.toArray(), labels.toArray().length, String[].class);    //Convert labels to array

            float[] array1 = model.getResultsPerCondition().get(labelsArray[0]);    //Fill the 4 float arrays;
            float[] array2 = model.getResultsPerCondition().get(labelsArray[1]);
            float[] array3 = model.getResultsPerCondition().get(labelsArray[2]);
            float[] array4 = model.getResultsPerCondition().get(labelsArray[3]);
            BoxPlot showBoxPlot = new BoxPlot(array1, array2, array3, array4, labelsArray, screenWidth, screenHeight);
            showBoxPlot.init();
            int width = (int) (500 * showBoxPlot.scaleFactor());
            int diff = (screenWidth - width) / 2;
            showBoxPlot.setBounds(diff, -20, screenWidth, ((int) 0.8 * screenHeight));   //Setbounds for correct position
            this.getContentPane().add(showBoxPlot);   //Show the results
        }

        if (o.toString().equals("Finished")) {
            this.setEnabled(false);
            this.dispose();
        }
    }
}
