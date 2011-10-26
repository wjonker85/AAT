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
 * This frame lets the test be executed fullscreen.
 */
public class TestFrame extends JFrame implements Observer {

    //Make it a fullscreen frame
    public TestFrame() {
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
        AATModel model = (AATModel) observable;

        if (o.toString().equals("Start")) {
            this.setVisible(true);
        }
        if (o.toString().equals("Test ended")) {
            System.out.println("Einde van de test");
            this.dispose();
            this.setEnabled(false);
            this.setVisible(false);

        }
    }
}
