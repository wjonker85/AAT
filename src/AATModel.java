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

    public static int Y_AXIS_CHANGED = 4;

    private int resize = 0;
    private long timeFirst = 0;

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


    //Methode die doorgeeft dat er een verandering van de Y-as is geweest
    public void changeYaxis(int value) {
        System.out.println("Resize "+value);
        if(value == 0) {
              timeFirst = System.currentTimeMillis();
            System.out.println("Oud "+timeFirst);
        }
        if(value ==3 || value == -3) {
            long reactionTime = System.currentTimeMillis() - timeFirst;
            System.out.println("Nieuw "+System.currentTimeMillis());
            System.out.println("Reaction Time: "+reactionTime);

        }
        resize = value;
        this.setChanged();

        notifyObservers("Y-as");
    }

    public int getPictureSize() {
        return resize;
    }

}
