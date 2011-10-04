import processing.core.PApplet;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AATView extends PApplet implements Observer {

    private AATModel model;

    public AATView() {
        setVisible(true);
    }

    public void update(Observable observable, Object o) {
        model = (AATModel) observable;
        if(o.equals("View changed")) {
            if(model.currentView() == AATModel.TEST_VIEW) {
                this.setVisible(true);
            }
            else {
                this.setVisible(false);
            }
        }

    }



}
