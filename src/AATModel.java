import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AATModel extends Observable {

    public static int TEST_VIEW = 0;
    public static int SINGLE_RESULTS = 1;
    public static int OVERALL_RESULTS = 3;

    private int currentView = 0;

    public AATModel() {

    }

    public void setCurrentView(int newView) {
        currentView = newView;
        this.notifyObservers("View changed");

    }

    public int currentView() {
        return currentView;
    }


}
