import javax.swing.table.TableModel;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * This model does all the logic for the AAT. It uses the Model-View-Controller pattern. So it does the communication between
 * the controllers and the views. Controllers are the joystick and the user input from the main Frame. Views are the test view and
 * results view.
 * All the information belonging to the test and progress of the test is kept in this model. A new AAT can be started with the given
 * number of runs, a break can be included after x runs. Neutral and affective cues can be chosen. Either a colored border. Different
 * image shapes or no visual cues created. In the last case the visual cues can be in the image. So that the test can have every
 * visual cue that is necessary.
 * <p/>
 * When a picture has to be pulled or pushed all joystick movements are recorded with their corresponding reaction times.
 * All the data for all participants are stored in a file and selections from those results can be exported so it can be analyzed
 * with R or SPSS. A researcher can choose the way the data has to be prepared. E.g. include mistakes or not.
 * <p/>
 * When a user has done the test it is possible that it can see his or her results graphically..
 * <p/>
 * <p/>
 * TODO: Informatie lezen uit configuratiebestand, dus alles nog wat dynamischer maken.
 * TODO: Iets doen met de verschillende opties van weergave.
 * TODO: Misschien een testplaatje als eerste
 * TODO: Nog eerst een testrun toevoegen.
 * TODO: Id verhogen als er een nieuwe test gestart wordt
 * TODO: Dat id moet dan 1 hoger zijn dan de hoogste die in het bestand staat.
 * TODO: Alle informatie uit het hele data bestand inlezen.
 * TODO: export functie met filter.
 * TODO: Uitbreiden met practice runs
 */
public class AATModel extends Observable {

    //Verschillende berichten die naar de views gestuurd kunnen worden.

    //Welke views dienen actief te worden
    public static int TEST_VIEW = 0;
    public static int SINGLE_RESULTS = 1;
    public static int OVERALL_RESULTS = 3;


    //Test status
    private static int TEST_STOPPED = 0;
    private static int IMAGE_LOADED = 1;
    private static int TEST_WAIT_FOR_TRIGGER = 2;

    private Hashtable<String, String> testOptions;


    private int resize = 5;     //Start with the middle of the joystick


    //Test variables
    private int repeat;
    private int breakAfter;
    private int count; //Counts the number of images shown.
    private int run;
    private int id = 0;
    private boolean hasBorder;
    private MeasureData newMeasure;
    private long startMeasure;
    private TestConfig testConfig;
    private TextReader textReader;

    private AATImage current;
    private int testStatus;

    private ArrayList<File> neutralImages;
    private ArrayList<File> affectiveImages;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.
    private Hashtable<Integer, String> colorTable;    //Contains the border colors
    private File languageFile;


    private Pattern pattern;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";        //regex for extension filtering


    //Constructor.
    public AATModel(File config) {
        testConfig = new TestConfig(new File("sampleConfig"));
        File neutralDir = new File("images" + File.separator + testConfig.getValue("AffectiveDir"));
        File affectiveDir = new File("images" + File.separator + testConfig.getValue("NeutralDir"));
        languageFile = new File(testConfig.getValue("LanguageFile"));
        textReader = new TextReader(languageFile);
        pattern = Pattern.compile(IMAGE_PATTERN);
        neutralImages = getImages(neutralDir);
        affectiveImages = getImages(affectiveDir);
        //  imageFiles = getImages(imageDir); //create ArrayList with all image files;
        testStatus = AATModel.TEST_STOPPED;
        if (testConfig.getValue("ColoredBorders").equals("True")) {
            colorTable = new Hashtable<Integer, String>();
            colorTable.put(AATImage.PULL, testConfig.getValue("BorderColorPull"));
            colorTable.put(AATImage.PUSH, testConfig.getValue("BorderColorPush"));
        }
    }


    //Loads all image files in a given directory. Extension filter with regular expression.
    private ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }


    //Create fileFilter based on regular expression.
    FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };

    /*
        Creates a randomised list, containing the affective and neutral images. Every image gets loaded twice, one for the
        pull condition and one for the push condition.
     */
    private ArrayList<AATImage> createRandomList() {
        ArrayList<AATImage> randomList = new ArrayList<AATImage>();
        for (File image : neutralImages) {                //Load the neutral images
            AATImage pull = new AATImage(image, AATImage.PULL, AATImage.NEUTRAL); //Two instances for every image
            randomList.add(pull);
            AATImage push = new AATImage(image, AATImage.PUSH, AATImage.NEUTRAL);
            randomList.add(push);
        }
        for (File image : affectiveImages) {    //Load the affective images
            AATImage pull = new AATImage(image, AATImage.PULL, AATImage.AFFECTIVE); //Two instances for every image
            randomList.add(pull);
            AATImage push = new AATImage(image, AATImage.PUSH, AATImage.AFFECTIVE);
            randomList.add(push);
        }
        Collections.shuffle(randomList);    //Randomise the list
        return randomList;
    }

    //Starts a new instance of the AAT. With no. times it has to repeat and when there will be a break.
    public void startTest() {
        this.repeat = Integer.parseInt(testConfig.getValue("Trails"));
        this.breakAfter = Integer.parseInt(testConfig.getValue("BreakAfter"));
        testList = createRandomList();
        count = 0;        //reset counters
        run = 0;
        newMeasure = new MeasureData(id);
        id++;          //new higher id
        this.setChanged();
        notifyObservers("Start");      //Notify the observer that a new test is started.
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Test status is wait for the user to press the trigger button.
    }

    //Which view is enabled
    public int getCurrentView() {
        int currentView;
        currentView = 0;
        return currentView;
    }

    /*
        This method determines the next step to be taken in the test.

    */
    private void NextStep() {
        if (count < testList.size()) {     //Just show the next image
            showNextImage();
        } else {          //No more images in the list
            run++;
            count = 0;
            if (run == breakAfter) {    //Test needs a break
                testList = createRandomList(); //create a new random list
                current = testList.get(0);
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                this.setChanged();
                notifyObservers("Break");      //Notify observer that there is a break.

            } else if (run == repeat) {   //No more runs left, Test has ended
                this.setChanged();
                notifyObservers("Test ended");
                testStatus = AATModel.TEST_STOPPED;    //Notify observers about it
                writeToFile();
            } else {           //Continue with a new run
                testList = createRandomList(); //create a new Random list
                count = 0;
                showNextImage();
            }
        }
    }

    //Show the next image in the list
    private void showNextImage() {
        current = testList.get(count);    //change current to the next image
        System.out.println("Loaded " + current.toString());
        this.setChanged();
        notifyObservers("Show Image");      //Notify observers
        startMeasure(); //Start the measurement.
        count++;
    }

    //Geeft een integer met de grootte van het plaatje.
    public int getPictureSize() {
        return resize;
    }

    //returns colors for the direction being asked (push or pull)
    public String getBorderColor(int direction) {
        return colorTable.get(direction);
    }

    public boolean hasColoredBorders() {
        if(testConfig.getValue("ColoredBorders").equals("True")) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getBreakText() {
        return textReader.getValue("Break");
    }

    public String getIntroductionText() {
        return textReader.getValue("Introduction");
    }

    public String getTestFinishedText() {
        return textReader.getValue("Finished");
    }

    //Returns the next Image
    public BufferedImage getNextImage() {
        return current.getImage();
    }

    //
    public int getStepRate() {
        return Integer.parseInt(testConfig.getValue("StepSize"));
    }

    public int getBorderWidth() {
        return Integer.parseInt(testConfig.getValue("BorderWidth"));
    }

    //Returns the current direction (Push of Pull)
    public int getDirection() {
        return current.getDirection();
    }

    //Start a new measure for every new image.
    public void startMeasure() {
        newMeasure.newMeasure(run, current.toString(), current.getDirection(), current.getType()); //Begin met de metingen opslaan.
        startMeasure = System.currentTimeMillis();  //Begintijd
    }

    //Get measurement.
    public long getMeasurement() {
        return System.currentTimeMillis() - startMeasure;
    }

    private void writeToFile() {
        CSVWriter writer = new CSVWriter(newMeasure.getAllResults());
        writer.writeData(new File("test.csv"));
    }

    //Display results and write them to a file
    public TableModel getResults() {

        return newMeasure.getAllResults();
        //   return newMeasure.getAllResults();
    }

    //--------------Input from the controller-------------------------------------//


    /*
        Gets changes in the movement from the y-axis of the joystick.
        Test status has to be changed when the joystick reaches the maximum distance. It depends on the direction that belongs to
        the current image. When the user performs the requested action, when finished the test status has to go to wait for trigger.
     */
    public void changeYaxis(int value) {
        if (value != resize) {
            resize = value;
            if (testStatus == AATModel.IMAGE_LOADED) { //Only listen when there is an image loaded
                newMeasure.addResult(resize, getMeasurement());     //add results to the other measurements
                if (current.getDirection() == AATImage.PULL && value == 9) {   //check if the requested action has been performed
                    removeImage();
                } else if (current.getDirection() == AATImage.PUSH && value == 1) {
                    removeImage();
                }
            }
            this.setChanged();
            notifyObservers("Y-as");
        }
    }

    //Remove image when the user has performed the action. Set test status to wait for trigger
    private void removeImage() {
        this.setChanged();
        notifyObservers("Wait screen");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
    }

    /*When the test is waiting for the trigger, check if the trigger is pressed and then change the test status
    to image loaded. then call nextstep so the model can determine the appropriate next action to take
    */
    public void triggerPressed() {
        if (testStatus == AATModel.TEST_WAIT_FOR_TRIGGER) {
            testStatus = AATModel.IMAGE_LOADED;
            NextStep();
        }
    }
}

class TestConfig {


    private Map<String, String> testOptions = new HashMap<String, String>();
    private File testConfig;
    //All the configuration options
    String[] options = {
            "ColoredBorders",
            "BorderColorPush",
            "BorderColorPull",
            "BorderWidth",
            "StepSize",
            "Trails",
            "BreakAfter",
            "AffectiveDir",
            "NeutralDir",
            "LanguageFile",
            "PracticeTrails"

    };

    //Constructor, fills the Hashtable with
    public TestConfig(File testConfig) {
        this.testConfig = testConfig;
        testOptions = new Hashtable<String, String>();
        for (int x = 0; x < options.length; x++) {
            testOptions.put(options[x], "");

        }
        readConfig();
    }

    private void readConfig() {
        String strLine;
        StringTokenizer st;
        try {
            BufferedReader br = new BufferedReader(new FileReader(testConfig));
            while ((strLine = br.readLine()) != null) {
                st = new StringTokenizer(strLine, " ");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.contains("#")) {
                        strLine = null;
                    }
                    if (testOptions.containsKey(token)) {
                        for (Map.Entry<String, String> entry : testOptions.entrySet()) {
                            if (entry.getKey().equals(token)) {
                                entry.setValue(st.nextToken());
                                strLine = null;
                                break;
                            }
                        }

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public String getValue(String key) {
        return testOptions.get(key);
    }
}

class TextReader {

    private Map<String, String> testText = new HashMap<String, String>();
    private File languageFile;

    //All the configuration options
    String[] options = {
            "Introduction",
            "Practice",
            "Break",
            "Finished"
    };

    public TextReader(File languageFile) {
        this.languageFile = languageFile;
        for (int x = 0; x < options.length; x++) {
            testText.put(options[x], "");
        }
        readConfig();
        test();
    }

    private String transform(String s) {
        s = s.replace("<", "");
        s = s.replace(">", "");
        return s;
    }

    private void readConfig() {
        String strLine;
        StringTokenizer st;
        boolean firstKey = true;
        String key = "";
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(languageFile));
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    strLine = null;
                }
                assert strLine != null;
                if (strLine.startsWith(("<"))) {
                    if (!strLine.startsWith("</")) {
                        key = transform(strLine);
                        text = "";
                    }
                } else {
                    if (testText.containsKey(key)) {
                        text += strLine+"\n";      //Read text lines
                    }
                }
                if (!strLine.startsWith("</")) {
                    for (Map.Entry<String, String> entry : testText.entrySet()) {       //Replace value
                        if (entry.getKey().equals(key)) {
                            entry.setValue(text);
                            strLine = null;
                            break;
                        }
                    }
                }
            }
            //  }
        } catch (Exception
                e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getValue(String key) {
        return testText.get(key);
    }

    private void test() {
        System.out.println(testText.keySet());
        System.out.println(testText.values());

    }

}