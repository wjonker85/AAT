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
public class AATResults extends PApplet implements Observer {

    private AATModel model;

    public AATResults() {
        setVisible(false);
     }

    public void draw(){
        display();
    }

    public void display() {
        background(255);
        fill(255, 0, 0);
        rect(10, 10, 10, 10);
    }

    public void update(Observable observable, Object o) {
        model = (AATModel) observable;
        if (o.equals("View changed")) {
            if (model.getCurrentView() == AATModel.SINGLE_RESULTS) {
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }
        }

     }
}
