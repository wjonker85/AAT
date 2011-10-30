import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

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
        this.setLayout(new GridBagLayout());
        qPanel = new QuestionPanel(model);
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

        if (o.toString().equals("Test ended")) {
            System.out.println("Einde van de test");
            this.dispose();
            this.setEnabled(false);
            this.setVisible(false);

        }
    }
}
