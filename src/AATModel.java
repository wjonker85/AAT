import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * Model voor de AAT. Dit model houdt alle data bij, bepaalt welke view actief dient te zijn. Geeft ook de plaatje door.
 * TODO: Informatie lezen uit configuratiebestand
 * TODO: Misschien een testplaatje als eerste
 * TODO:
 */
public class AATModel extends Observable {

    //Verschillende berichten die naar de views gestuurd kunnen worden.

    //Welke views dienen actief te worden
    public static int TEST_VIEW = 0;
    public static int SINGLE_RESULTS = 1;
    public static int OVERALL_RESULTS = 3;


    //Status van de test.
    private static int TEST_STOPPED = 0;
    private static int IMAGE_LOADED = 1;
    private static int TEST_WAIT_FOR_TRIGGER = 2;


    private int resize = 5;     //Begint met het midden van de joystick.
    private long startMeasure;

    private int repeat;
    private int breakAfter;
    private int count; //Counts the number of images shown.   4
    private int run;
    private int id = 0;
    private MeasureData newMeasure;

    private AATImage current;
    private int testStatus;

    private ArrayList<File> neutralImages;
    private ArrayList<File> affectiveImages;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.
    private Hashtable<Integer, float[]> colorTable;
    private Pattern pattern;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";        //regex for extension filtering


    //Constructor.
    public AATModel() {
        File neutralDir = new File("images"+File.separator+"Neutral");
        File affectiveDir = new File("images"+File.separator+"Affective");
        pattern = Pattern.compile(IMAGE_PATTERN);
        neutralImages = getImages(neutralDir);
        affectiveImages = getImages(affectiveDir);
      //  imageFiles = getImages(imageDir); //create ArrayList with all image files;
        testStatus = AATModel.TEST_STOPPED;
        colorTable = new Hashtable<Integer, float[]>();
    }


    //Geeft een arrayList gevuld met alle grafische bestanden in een directory
    private ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }


    //Regular expression gebruiken om de files te filteren
    FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };

    //Maakt een lijst met AATImage objecten. Maakt van elk plaatje een push en pull versie en maakt er vervolgens een random lijst
    //van.
    private ArrayList<AATImage> createRandomList() {
        ArrayList<AATImage> randomList = new ArrayList<AATImage>();
        for (File image : neutralImages) {
            AATImage pull = new AATImage(image,AATImage.PULL,AATImage.NEUTRAL); //Two instances for every image
            randomList.add(pull);
            AATImage push = new AATImage(image, AATImage.PUSH, AATImage.NEUTRAL);
            randomList.add(push);
        }
        for (File image : affectiveImages) {
            AATImage pull = new AATImage(image,AATImage.PULL,AATImage.AFFECTIVE); //Two instances for every image
            randomList.add(pull);
            AATImage push = new AATImage(image, AATImage.PUSH, AATImage.AFFECTIVE);
            randomList.add(push);
        }
        Collections.shuffle(randomList);
        return randomList;
    }

    //Start een nieuwe AAT. Met het aantal keer herhalingen en wanneer er een pauze moet volgen.
    public void startTest(int repeat, int breakAfter) {
        this.repeat = repeat;
        this.breakAfter = breakAfter;
        testList = createRandomList();
        count = 0;
        run = 0;
        newMeasure = new MeasureData(id);
        id++;          //ID verhogen
        colorTable.put(AATImage.PULL, new float[]{0, 164, 231});
        colorTable.put(AATImage.PUSH, new float[]{245,254,2});
        this.setChanged();
        notifyObservers("Start");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Pas verder gaan als er op de trigger wordt gedrukt

    }


    public int getCurrentView() {
        int currentView;
        currentView = 0;
        return currentView;
    }

    /*
        Deze methode bepaalt wat de volgende stap in de test is. Deze methode wordt telkens aangeroepen nadat op de
        trigger knop gedrukt is. Als nog niet alle plaatjes in de lijst getoond worden, moet de test doorgaan met de volgende.
        Is het einde van de lijst bereikt, dan moet er gekeken worden of er gewoon verder gegaan moet worden met een nieuwe lijst,
        of er een pauze gehouden moet worden, of dat de test afgelopen is.

    */
    private void NextStep() {
        if (count < testList.size()) {     //De run is nog niet afgelopen, lijst bevat nog plaatjes.
            showNextImage();
        } else {
            run++;
            count = 0;
            if (run == breakAfter) {    //Test is bij de break aangekomen.
                testList = createRandomList(); //Nieuwe random lijst maken.
                current = testList.get(0);
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                this.setChanged();
                notifyObservers("Break");

            } else if (run == repeat) {   //Laatste run is geweest
                this.setChanged();
                notifyObservers("Test ended");
                testStatus = AATModel.TEST_STOPPED;
            } else {           //Verder gaan met een volgende run
                testList = createRandomList(); //create a new Random list
                count = 0;
                showNextImage();
            }
        }
    }

    //Volgend plaatje uit de lijst laten zien.
    private void showNextImage() {
        current = testList.get(count);    //Volgend plaatje laden.
        System.out.println("Loaded " + current.toString());
        this.setChanged();
        notifyObservers("Show Image");
        startMeasure(); //Begin de meting
        count++;
    }

    //Geeft een integer met de grootte van het plaatje.
    public int getPictureSize() {
        return resize;
    }

    //returns colors for the direction being asked (push or pull)
    public float[] getBorderColor(int direction) {
           return colorTable.get(direction);
    }

    //Returns the next Image
    public BufferedImage getNextImage() {
        return current.getImage();
    }

    public int getStepRate() {
        return 9;
    }

    //Returned of het om een push of pull image gaat.
    public int getDirection() {
        return current.getDirection();
    }

    //Start de meting zodra de view het plaatje geladen heeft.
    public void startMeasure() {
        newMeasure.newMeasure(run,current.toString(),current.getDirection(),current.getType()); //Begin met de metingen opslaan.
        startMeasure = System.currentTimeMillis();  //Begintijd
    }

    //Geeft een meting
    public long getMeasurement() {
        return System.currentTimeMillis() - startMeasure;
    }


    //Geeft een TableModel met de resultaten van een test.
    public TableModel getResults() {
        CSVWriter writer = new CSVWriter(newMeasure.getSimpleResults());
        writer.writeData(new File("test.csv"));
        return newMeasure.getSimpleResults();
     //   return newMeasure.getAllResults();
    }

    //--------------Input vanuit de Controller-------------------------------------//


    //Methode die doorgeeft dat er een verandering van de Y-as is geweest
    //Kijkt ook of het de controller in de maximum stand is geplaatst
    public void changeYaxis(int value) {
        if (value != resize) {
            resize = value;
            if (testStatus == AATModel.IMAGE_LOADED) { //alleen uitvoeren als de view een image geladen heeft.
                newMeasure.addResult(resize,getMeasurement());     //Resultaat toevoegen
                if (current.getDirection() == AATImage.PULL && value == 9) {   //Testen of het plaatje helemaal weggedrukt is.
                    removeImage();
                } else if (current.getDirection() == AATImage.PUSH && value == 1) {
                    removeImage();
                }
            }
            this.setChanged();
            notifyObservers("Y-as");
        }
    }

    //Aan de view doorgeven dat het plaatje weggedrukt is en de meting stoppen.
    private void removeImage() {
        this.setChanged();
        notifyObservers("Wait screen");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
    }

    //Als de trigger ingedruk wordt, gaat de status naar IMAGE_LOADED
    public void triggerPressed() {
        if (testStatus == AATModel.TEST_WAIT_FOR_TRIGGER) {
            testStatus = AATModel.IMAGE_LOADED;
            NextStep();
        }
    }
}


