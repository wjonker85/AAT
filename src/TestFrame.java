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
    private AATViewResults aatResults;
    private AATModel model;
    private QuestionPanel qPanel;

    //Make it a fullscreen frame
    public TestFrame(AATModel model) {
        this.model = model;
      //  this.setLayout(new GridBagLayout());
        this.getContentPane().setLayout(new GridBagLayout());
        qPanel = new QuestionPanel(model);
        this.setBackground(Color.black);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setEnabled(true);
        setVisible(false);
    }


    /*
            When a new test is started, this frame has to show the test. Frame gets
            disposed when the test has ended.

     */
    public void update(Observable observable, Object o) {
        if (o.toString().equals("Start")) {
            this.getContentPane().removeAll();
            this.setVisible(true);
            this.getContentPane().remove(qPanel);
            this.setLayout(null);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) dim.getWidth();
            int screenHeight = (int) dim.getHeight();
            if (aatView != null) {
                model.deleteObserver(aatView);
            }
            aatView = new AATView(model, screenHeight, screenWidth);
            aatView.init();             //Make sure the PApplet gets initialised.
            model.addObserver(aatView);    //Add a new aatView to the observers list.
            this.getContentPane().add(aatView);
            model.startTest();

        }

        if (o.toString().equals("Show questions")) {
            this.setVisible(true);
            System.out.println("Show questions");
            this.getContentPane().setBackground(Color.black);
            //   this.add(qPanel);
            this.getContentPane().add(qPanel, new GridBagConstraints());
            qPanel.displayQuestions(model.getExtraQuestions());
            //  this.setVisible(true);
        }

        if (o.toString().equals("Display results")) {
            this.getContentPane().removeAll();
        //    this.revalidate();
          //  this.removeAll();
            System.out.println("Display results");
            model.deleteObserver(aatView);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) dim.getWidth();
            int screenHeight = (int) dim.getHeight();
        //    this.getContentPane().setLayout(null);
           this.setLayout(null);
            Set labels = model.getResultsPerCondition().keySet();        //Get results from the model
            String[] labelsArray = Arrays.copyOf(labels.toArray(), labels.toArray().length, String[].class);    //Convert labels to array

            float[] array1 = model.getResultsPerCondition().get(labelsArray[0]);    //Fill the 4 float arrays;
            float[] array2 = model.getResultsPerCondition().get(labelsArray[1]);
            float[] array3 = model.getResultsPerCondition().get(labelsArray[2]);
            float[] array4 = model.getResultsPerCondition().get(labelsArray[3]);
            BoxPlot showBoxPlot = new BoxPlot(array1, array2, array3, array4, labelsArray, screenWidth, screenHeight);
            showBoxPlot.init();
            int width = (int) (500*showBoxPlot.scaleFactor());
            int diff = (screenWidth -width)/2;
            showBoxPlot.setBounds(diff,-20,screenWidth,((int) 0.8*screenHeight));   //Setbounds for correct position
            this.getContentPane().add(showBoxPlot);   //Show the results
        }
    }
}
