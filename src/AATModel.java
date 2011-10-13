import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static int TEST_RUNNING = 0;
    private static int TEST_ON_BREAK = 1;
    private static int TEST_ENDED = 2;

    public static int PUSH = 0;
    public static int PULL = 1;
    private int resize = 0;
    private long timeFirst = 0;

    private int currentView = 0;
    private long startMeasure;

    private int repeat;
    private int breakAfter;

    private int count; //Counts the number of images shown.   4
    private int run;
    private AATImage current;
    private int testStatus;


    private ArrayList<File> imageFiles;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.
    private File imageDir;
    private Matcher matcher;
    private Pattern pattern;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";        //regex for extension filtering

    private int nextImageType = 0;

    public AATModel() {
        imageDir = new File("images");
        pattern = Pattern.compile(IMAGE_PATTERN);
        imageFiles = getImages(imageDir); //create ArrayList with all image files;
    }


    //Returns arrayList with all imageFiles
    private ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }


    //Creates a random list based on the imageFiles.
    private ArrayList<AATImage> createRandomList(ArrayList<File> imageFiles) {
        ArrayList<AATImage> randomList = new ArrayList<AATImage>();
        for (File image : imageFiles) {
            AATImage pull = new AATImage(image, AATImage.PULL); //Two instances for every image
            randomList.add(pull);
            AATImage push = new AATImage(image, AATImage.PUSH);
            randomList.add(push);
        }
        Collections.shuffle(randomList);
        return randomList;
    }

    public void startTest(int repeat, int breakAfter) {
        this.repeat = repeat;
        this.breakAfter = breakAfter;
        testList = createRandomList(imageFiles);
        count = 0;
        run = 1;
        setCurrentImage(count); //Gets the first image
        count++;
        testStatus = AATModel.TEST_RUNNING;
        this.setChanged();
        notifyObservers("Start");
    }

    private void setCurrentImage(int pos) {
        current = testList.get(pos);
    }

    //Use regular expression for the filtering of extensions
    FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };


    public void setCurrentView(int newView) {
        currentView = newView;
        this.notifyObservers("View changed");

    }

    public int getCurrentView() {
        return currentView;
    }


    //Methode die doorgeeft dat er een verandering van de Y-as is geweest
    //TODO nog wat netter/verbeteren.
    public void changeYaxis(int value) {
      //  System.out.println("Resize " + value);
        resize = value;

        if (current.getType() == AATImage.PULL && value == 7) {
           nextImage();

        } else if (current.getType() == AATImage.PUSH && value == 1) {
            nextImage();
        }

        this.setChanged();
        notifyObservers("Y-as");
    }

    //Loads the next image if there are any left. Else the run has ended.
    //Load new run if there is no break or the test has ended.
    private void nextImage() {
        count++;
        if(count%(run*testList.size()) < testList.size()) {     //There are more images in the list
            current = testList.get(count);
                    this.setChanged();
            notifyObservers("Next Image");
        }
       else {
          if(count/(run*testList.size()) == breakAfter) {
              testList = createRandomList(imageFiles); //create a new Random list
              run++;
              current = testList.get(count%repeat);
              testStatus = AATModel.TEST_ON_BREAK;
             this.setChanged();
              notifyObservers("Break");
          }
          else if(count/(run*testList.size()) == repeat) {
              testStatus = AATModel.TEST_ENDED;
              this.setChanged();
              notifyObservers("Test ended");
          }
          else {
              testList = createRandomList(imageFiles); //create a new Random list
              current = testList.get(count%repeat);
              run++;
              this.setChanged();
              notifyObservers("Next Image");
          }
        }
    }


    public int getPictureSize() {
        return resize;
    }


    //Start de meting zodra de view het plaatje geladen heeft.
    public void startMeasure() {
        startMeasure = System.currentTimeMillis();
    }

    public BufferedImage getImage() {
        return null;
    }

    public int getDirection() {
        return 0;
    }

    //Trigger pressed
    public void triggerPressed() {
        //    System.out.println("Trigger pressed");
        if(testStatus == AATModel.TEST_ON_BREAK) {         //end the break
            testStatus = AATModel.TEST_RUNNING;
            this.setChanged();
            notifyObservers("Resumed");
        }
        this.setChanged();
        notifyObservers("Trigger");
    }


    //Getter/Setter for nextImage type (push or pull)
    private void setNextImageType(int type) {
        nextImageType = type;
    }

    public int getNextImageType() {
        return current.getType();
    }

    //Returns the next Image
    public BufferedImage getNextImage() {
        return current.getImage();
    }
}


//Class that contains the image that has to be displayed. It also contain whether it's a pull or push image
