import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * Model voor de AAT. Dit model houdt alle data bij, bepaalt welke view actief dient te zijn. Geeft ook de plaatje door.
 * TODO Alleen next image als de juiste beweging wordt gemaakt.
 * TODO; Map met alle plaatjes laden. Daarna random verdelen.
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


    public static int PUSH = 0;
    public static int PULL = 1;
    private int resize = 0;
    private long timeFirst = 0;

    private int currentView = 0;
    private long startMeasure;
    private ArrayList<Image> Images;

    public AATModel() {
        Images = new ArrayList<Image>();
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
        System.out.println("Resize " + value);
        if (value == 1 || value == 7) {
            this.setChanged();
            notifyObservers("Black Screen");
        }
        resize = value;
        this.setChanged();

        notifyObservers("Y-as");

        System.out.println("public void changeYaxis(int value): " + value);
    }

    public int getPictureSize() {
        return resize;
    }


    //Start de meting zodra de view het plaatje geladen heeft.
    public void startMeasure() {
        startMeasure = System.currentTimeMillis();
    }

    public Image getImage() {
        return null;
    }

    public int getDirection() {
        return 0;
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

class AATImage {

    private int type;
    private Image image;

    public AATImage(Image image, int type) {
        this.type = type;
        this.image = image;

    }

    public int getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }
}