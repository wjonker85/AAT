import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
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

    private static int IMAGE_LOADED = 0;
    private static int TEST_ON_BREAK = 1;
    private static int TEST_ENDED = 2;
    private static int TEST_WAIT_FOR_TRIGGER = 3;
    private static int TEST_START_INFO;

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


    private ArrayList<Double> results;
    private boolean imageLoaded = false;
    private ArrayList<File> imageFiles;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.
    private Pattern pattern;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";        //regex for extension filtering

    public AATModel() {
        File imageDir = new File("images");
        pattern = Pattern.compile(IMAGE_PATTERN);
        imageFiles = getImages(imageDir); //create ArrayList with all image files;
        results = new ArrayList<Double>();
    }


    //Geeft een arrayList gevuld met alle grafische bestanden in een directory
    private ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }


    //Maakt een lijst met AATImage objecten. Maakt van elk plaatje een push en pull versie en maakt er vervolgens een random lijst
    //van.
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

    //Start een nieuwe AAT. Met het aantal keer herhalingen en wanneer er een pauze moet volgen.
    public void startTest(int repeat, int breakAfter) {
        this.repeat = repeat;
        this.breakAfter = breakAfter;
        testList = createRandomList(imageFiles);
        count = 0;
        run = 0;
        this.setChanged();
        notifyObservers("Start");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
    }

    //Use regular expression for the filtering of extensions
    FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
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
        if (testStatus == AATModel.IMAGE_LOADED) { //alleen uitvoeren als de view een image geladen heeft.
            if (current.getType() == AATImage.PULL && value == 9) {
                this.setChanged();
                endMeasure();
                notifyObservers("Wait screen");  //Doorgeven dat het plaatje weg is.
            } else if (current.getType() == AATImage.PUSH && value == 1) {
                this.setChanged();
                endMeasure();
                notifyObservers("Wait screen");
            }
        }
        this.setChanged();
        notifyObservers("Y-as");

    }

  //  public void imageLoaded() {
  //      System.out.println("Plaatje geladen");
   //     imageLoaded = true;
   //     startMeasure();
  //  }

    private void loadNextImage() {
        if (count < testList.size()) {     //De run is nog niet afgelopen, lijst bevat nog plaatjes.
            current = testList.get(count);    //Volgend plaatje laden.
            System.out.println("Loaded "+current.toString());
            count++;
            this.setChanged();
            notifyObservers("Show Image");
        } else {      //Geen plaatjes meer om te laten zien, nu kijken of er een break is, of er direct nieuwe plaatjes getoond kunnen
             run++;     //of dat de test afgelopen is.
            if (run == breakAfter) {    //Test is bij de break aangekomen.
                testList = createRandomList(imageFiles); //Nieuwe random lijst maken.
                current = testList.get(count % repeat);
                testStatus = AATModel.TEST_ON_BREAK;
                count = 0;
                this.setChanged();
                notifyObservers("Break");

            } else if (run == repeat) {   //Laatste run is geweest
                testStatus = AATModel.TEST_ENDED;
                count = 0;
                System.out.println("No results " + results.size());
                for (Double d : results) {
                    System.out.println(d);
                }
                this.setChanged();
                notifyObservers("Test ended");
            } else {           //Gewoon doorgaan met een volgende run
                testList = createRandomList(imageFiles); //create a new Random list
                current = testList.get(count % repeat); //TODO: nog even naar kijken of dit wel klopt.
                count = 0;
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

    public void endMeasure() {
        double reactionTime = System.currentTimeMillis() - startMeasure;
        System.out.println(System.currentTimeMillis() - startMeasure);
        //   System.out.println("result added");
        results.add(reactionTime);
    }

    //Als de trigger ingedruk wordt, gaat de status naar IMAGE_LOADED
    public void triggerPressed() {
        //    System.out.println("Trigger pressed");
        if (testStatus == AATModel.TEST_ON_BREAK) {         //end the break
            testStatus = AATModel.IMAGE_LOADED;
            loadNextImage();
        //    this.setChanged();
        //    notifyObservers("Show Image");
        }
        if (testStatus == AATModel.TEST_WAIT_FOR_TRIGGER) {
            this.setChanged();
            testStatus = AATModel.IMAGE_LOADED;
        //    loadNextImage();
       //     notifyObservers("Show Image");
        }
        if (testStatus == AATModel.TEST_START_INFO) {
            this.setChanged();
            testStatus = IMAGE_LOADED;
            loadNextImage();
            notifyObservers("Show Image");
        }

    }

    //Returns the next Image
    public BufferedImage getNextImage() {
        return current.getImage();
    }
}

//class resultData {

//  private ArrayList runs;

//   public resultData() {

//   }

//  public void add

//}

