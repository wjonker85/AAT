import java.awt.*;
import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * Model voor de AAT. Dit model houdt alle data bij, bepaalt welke view actief dient te zijn. Geeft ook de plaatje door.
 */
public class AATModel extends Observable {

    //Verschillende berichten die naar de views gestuurd kunnen worden.

    //Welke views dienen actief te worden
    public static int TEST_VIEW = 0;
    public static int SINGLE_RESULTS = 1;
    public static int OVERALL_RESULTS = 3;

    //Berichten voor tijdens de test.
    public static int Y_AXIS_CHANGED = 4;
    public static int JOYSTICK_BUTTON_PRESSED = 5;
    public static int NEXT_PICTURE = 6;

    private int resize = 0;
    private long timeFirst = 0;

    private int currentView = 0;
    private long startMeasure;

    public AATModel() {

    }

    public void setCurrentView(int newView) {
        currentView = newView;
        this.notifyObservers("View changed");

    }

    public int getCurrentView() {
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

    //Start de meting zodra de view het plaatje geladen heeft.
    public void startMeasure() {
         startMeasure = System.currentTimeMillis();
    }

    public void triggerPressed() {
    //    System.out.println("Trigger pressed");
       this.setChanged();
        notifyObservers("Trigger");
    }

    //Returns the next Image
    public Image getNextImage() {
        return null;
    }
}
