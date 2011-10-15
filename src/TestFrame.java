import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestFrame extends JFrame implements Observer {

    public TestFrame() {
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setEnabled(true);
        setVisible(false);
    }


    public void update(Observable observable, Object o) {
        AATModel model = (AATModel) observable;

        if (o.toString().equals("Start")) {
            //         if (model.getCurrentView() == AATModel.TEST_VIEW) {
//            this.setEnabled(true);
            this.setVisible(true);
            //       } else {
            //       this.setVisible(false);
            //     }
        }
        if (o.toString().equals("Test ended")) {
            System.out.println("Einde van de test");
            this.dispose();
            this.setEnabled(false);
            this.setVisible(false);

        }
    }
}
