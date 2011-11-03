package Model;

import Configuration.TestConfig;
import Configuration.TextReader;
import DataStructures.AATImage;
import DataStructures.DynamicTableModel;
import DataStructures.MeasureData;
import DataStructures.QuestionObject;
import io.CSVReader;
import io.CSVWriter;

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
 * When a user has done the test he can see his results with four boxplots. One for every condition.
 * <p/>
 * <p/>
 * <p/>   TODO checken voor borderwidth, bordercolor, stepsize
 */

public class AATModel extends Observable {

    //Configuration readers
    private TestConfig testConfig;
    private TextReader textReader;

    //Test status
    private static int TEST_STOPPED = 0;
    private static int TEST_WAIT_FOR_QUESTIONS = 1;
    private static int PRACTICE_IMAGE_LOADED = 2;
    private static int TEST_WAIT_FOR_TRIGGER = 3;
    private static int IMAGE_LOADED = 4;
    private static int TEST_SHOW_FINISHED = 5;
    private static int TEST_SHOW_RESULTS = 6;


    //Test variables
    private int repeat;
    private int breakAfter;
    private int practiceRepeat;
    private int count; //Counts the number of images shown.

    //Test view variables
    private int borderWidth;
    private int stepSize;
    private String practiceFillColor;

    //progress variables
    private int practiceCount;
    private int run;
    private int id = 0;
    private int resize;
    private boolean practice;

    //Measurement
    private MeasureData newMeasure;
    private long startMeasure;

    //Current image and current status of the test
    private AATImage current;
    private int testStatus;

    //Different lists, containing the files
    private ArrayList<File> neutralImages;
    private ArrayList<File> affectiveImages;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.
    private ArrayList<AATImage> practiceList;

    private File practiceDir;

    //Hashmaps containing color information and the optional questions
    private Hashtable<Integer, String> colorTable;    //Contains the border colors
    private HashMap<String, String> extraQuestions;
    private ArrayList<QuestionObject> questionsList;

    private DynamicTableModel dynamic;

    //Files where the data needs to be saved to.
    private String workingDir;
    private File participantsFile;
    private File dataFile;


    //regex for extension filtering
    private Pattern pattern;
    private Pattern hexPattern;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    private static final String HEX_PATTERN = "(^[0-9A-F]+$)";

    //Constructor.
    public AATModel() {
        pattern = Pattern.compile(IMAGE_PATTERN); //create regex
        hexPattern = Pattern.compile(HEX_PATTERN);
        //   testStatus = AATModel.TEST_STOPPED;
    }

//------------------------------initialise AAT --------------------------------------------------

    //Starts a new instance of the AAT. With no. times it has to repeat and when there will be a break.
    public void startTest() {

        if (hasPractice()) {        //If set in the config, first do a practice.
            practice = true;
            repeat++;     //Make these one higher, because of the practice
            breakAfter++;
            System.out.println("Heeft practice");
            practiceCount = 0; //reset the counter
        }

        testList = createRandomList();
        count = 0;        //reset counters
        run = 0;
        id = getHighestID(this.participantsFile);
        id++;          //new higher id
        newMeasure = new MeasureData(id);

        if (questionsList.size() > 0) {   //When there are extra question, show them
            testStatus = AATModel.TEST_WAIT_FOR_QUESTIONS;
            this.setChanged();
            notifyObservers("Show questions");      //Notify the observer that a new test is started.
        } else {
            addParticipantsData();
            this.setChanged();
            notifyObservers("Start");
            testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Start the test
        }
    }

    /*
    Load all the configuration data from the config file and the language file specified in that config
    Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     */
    public void loadConfig(File config) throws FalseConfigException {   //TODO bladiebla
        dynamic = new DynamicTableModel();
        testConfig = new TestConfig(config);
        workingDir = config.getParentFile().getAbsolutePath();
        String partFile = testConfig.getValue("ParticipantsFile");
        if (partFile.equals("")) {
            throw new FalseConfigException("Participants file is not set");
        }
        String datFile = testConfig.getValue("DataFile");
        if (datFile.equals("")) {
            throw new FalseConfigException("Data file is not set");
        }
        participantsFile = new File(workingDir + File.separator + partFile);
        dataFile = new File(workingDir + File.separator + datFile);

        String langFile = testConfig.getValue("LanguageFile");
        File languageFile = new File(workingDir + File.separator + langFile);
        if (langFile.equals("") || !languageFile.exists()) {
            throw new FalseConfigException("No language file specified");
        }

        id = getHighestID(participantsFile);
        String nDir = testConfig.getValue("NeutralDir");
        String aDir = testConfig.getValue("AffectiveDir");
        File neutralDir = new File(workingDir + File.separator + aDir);
        File affectiveDir = new File(workingDir + File.separator + nDir);
        if (nDir.equals("") || !neutralDir.isDirectory()) {
            throw new FalseConfigException("Directory for the neutral images is not set properly");
        }
        if (aDir.equals("") || !affectiveDir.isDirectory()) {
            throw new FalseConfigException("Directory for the affective images is not set properly");
        }

        textReader = new TextReader(languageFile);
        neutralImages = getImages(neutralDir);
        if (neutralImages.size() == 0) {
            throw new FalseConfigException("Neutral images directory contains no images");
        }
        affectiveImages = getImages(affectiveDir);
        if (affectiveImages.size() == 0) {
            throw new FalseConfigException("Affective images directory contains no images");
        }
        questionsList = textReader.getExtraQuestions();

        String doBorders = testConfig.getValue("ColoredBorders");
        if (!doBorders.equals("True") && !doBorders.equals("False")) {
            throw new FalseConfigException("ColoredBorders has a false value, must be True or False");
        }

        if (testConfig.getValue("ColoredBorders").equals("True")) {
            colorTable = new Hashtable<Integer, String>();
            String pullColor = testConfig.getValue("BorderColorPull");
            Matcher matcher = hexPattern.matcher(pullColor);
            if (!(pullColor.length() == 6) || !matcher.matches()) {
                throw new FalseConfigException("The color specified for the pull border is not a valid 6 character hex value");
            }

            String pushColor = testConfig.getValue("BorderColorPush");
            matcher = hexPattern.matcher(pushColor);
            if (!(pushColor.length() == 6) || !matcher.matches()) {
                throw new FalseConfigException("The color specified for the push border is not a valid 6 character hex value");
            }
            colorTable.put(AATImage.PULL, "FF" + testConfig.getValue("BorderColorPull"));
            colorTable.put(AATImage.PUSH, "FF" + testConfig.getValue("BorderColorPush"));  //Also add alpha channel for processing
            try {
                borderWidth = Integer.parseInt(testConfig.getValue("BorderWidth"));
            } catch (Exception e) {
                throw new FalseConfigException("Border width is not configured properly");
            }
        }
        try {
            repeat = Integer.parseInt(testConfig.getValue("Trails"));
        } catch (Exception e) {
            throw new FalseConfigException("Number of trails is not configured properly");
        }
        try {
            breakAfter = Integer.parseInt(testConfig.getValue("BreakAfter"));
        } catch (Exception e) {
            throw new FalseConfigException("BreakAfter is not configured properly");
        }
        try {
            stepSize = Integer.parseInt(testConfig.getValue("StepSize"));
        } catch (Exception e) {
            throw new FalseConfigException("StepSize is not configured properly");
        }
        if (!testConfig.getValue("PracticeRepeat").equals("")) {  //When a value for practice repeat is set, check validity
            try {

                practiceRepeat = Integer.parseInt(testConfig.getValue("PracticeRepeat"));
            } catch (Exception e) {
                throw new FalseConfigException("PracticeRepeat is not configured properly");
            }
            if (practiceRepeat > 0) {
                String practDir = testConfig.getValue("PracticeDir");
                if (practDir.equals("")) {
                    practiceFillColor = testConfig.getValue("PracticeFillColor");
                    Matcher matcher;
                    hexPattern.matcher(practiceFillColor);
                    matcher = hexPattern.matcher(practiceFillColor);
                    if (!(practiceFillColor.length() == 6) || !matcher.matches()) {
                        throw new FalseConfigException("The color specified for the practice image fill color is not a valid 6 character hex value");
                    }
                } else {
                    practiceDir = new File(practDir);
                    if (!practiceDir.isDirectory()) {
                        throw new FalseConfigException("The directory for the practice images is nog properly configured");
                    }
                }
                practiceList = createRandomPracticeList(practiceRepeat);
                if (practiceList.size() == 0) {
                    throw new FalseConfigException("Practice images directory contains no images");
                }
                practice = true;
            }


        }
    }


    public class FalseConfigException extends Exception {

        public FalseConfigException(String error) {
            super(error);
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

    //Returns the hightest ID given to a participant so far.
    private int getHighestID(File file) {
        if (file.exists()) {
            CSVReader csvReader = new CSVReader(file);
            int columns = csvReader.getColumnNames().size();
            int dataSize = csvReader.getData().size();
            return Integer.parseInt(csvReader.getData().get(dataSize - columns).toString());    //Return first element from the last row.
        } else {
            return 0;
        }
    }

    /*
    Creates a random list with practice images. If there is a directory specified in the config, it will load the
    imageFile from that directory. Otherwise it will generate a list with self-created images containing a single color
    as specified in the config.
     */
    private ArrayList<AATImage> createRandomPracticeList(int size) {
        ArrayList<AATImage> list = new ArrayList<AATImage>();
        if (practiceDir != null) {  //image dir is set
            String practiceDir = testConfig.getValue("PracticeDir");

            for (int x = 0; x < size; x++) {
                for (File image : getImages(new File(practiceDir))) {
                    AATImage pull = new AATImage(image, AATImage.PULL, AATImage.PRACTICE); //Add push and pull version
                    AATImage push = new AATImage(image, AATImage.PUSH, AATImage.PRACTICE);
                    list.add(pull);
                    list.add(push);
                }
            }
        } else {             //Else let the test create the images itself
            Color c;
            if (practiceFillColor.length() > 0) {
                c = Color.decode("#" + practiceFillColor);
            } else {
                c = Color.gray;
            }

            int i = 0;
            for (int x = 0; x < size; x++) {
                AATImage pull = new AATImage(AATImage.PULL, c, i);
                list.add(pull);
                i++;
                AATImage push = new AATImage(AATImage.PUSH, c, i);
                list.add(push);
                i++;
            }
        }
        Collections.shuffle(list); //randomise the list.
        return list;
    }

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


    //Checks the configuration if a practice trail is needed
    private boolean hasPractice() {
        String s = testConfig.getValue("PracticeRepeat");
        if (s.equals("") || s.equals("0")) {
            return false;
        }
        return true;
    }


//---------------------------Test Progress---------------------------------------------------------

    /*
        This method determines the next step to be taken in the test.
        Based on testStatus and the current image shown to the participant
        Makes sure that the correct images are shown(practice/real test). Puts the test on break
        if that's necessary. And watches when the test is finished. Together with the joystick inputs this method
        determines the progress of the AAT.
    */
    private void NextStep() {
        if (testStatus == AATModel.PRACTICE_IMAGE_LOADED) {
            if (practiceCount < practiceList.size()) {
                showNextImage();
            } else {
                run++;
                practice = false; //Practice has ended
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                this.setChanged();
                this.notifyObservers("Practice ended");

            }
        } else {
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
                    //    System.out.println("Einde van de test");
                    testStatus = AATModel.TEST_STOPPED;    //Notify observers about it
                    writeToFile();
                    this.setChanged();
                    notifyObservers("Wait screen");   //First show black screen
                } else {           //Continue with a new run
                    testList = createRandomList(); //create a new Random list
                    count = 0;
                    showNextImage();
                }
            }
        }
    }

    //Show the next image in the list
    private void showNextImage() {
        if (practice) {
            current = practiceList.get(practiceCount);
            practiceCount++;
        } else {
            current = testList.get(count);    //change current to the next image
            count++;
        }
        this.setChanged();
        notifyObservers("Show Image");      //Notify observers
        startMeasure(); //Start the measurement.
    }

    //Returns the next Image
    public BufferedImage getNextImage() {
        return current.getImage();
    }

//---------------------Getter and Setter methods for the different Observers and Controllers -------------


    //Return the total number of images a participant gets shown
    public int getTotalImageCount() {
        int affective = affectiveImages.size();
        int neutral = neutralImages.size();
        int runCount = Integer.parseInt(testConfig.getValue("Trails"));
        return 2 * (affective + neutral) * runCount;
    }


    //Geeft een integer met de grootte van het plaatje.
    public int getPictureSize() {
        return resize;
    }

    //returns colors for the direction being asked (push or pull)
    public String getBorderColor(int direction) {
        return colorTable.get(direction);
    }

    //Returns whether the pictures should have a colored border
    public boolean hasColoredBorders() {
        if (testConfig.getValue("ColoredBorders").equals("True")) {
            return true;
        } else {
            return false;
        }
    }

    public String getBreakText() {
        return textReader.getValue("Break");
    }

    public String getIntroductionText() {
        return textReader.getValue("Introduction");
    }

    public String getTestStartText() {
        return textReader.getValue("Start");
    }

    public String getTestFinishedText() {
        return textReader.getValue("Finished");
    }

    //In how many steps does an image gets resized
    public int getStepRate() {
        return stepSize;
    }

    //returns the width of the border
    public int getBorderWidth() {
        return borderWidth;
    }

    //Read the optional extra questions from the language file specified in the configuration file.
    public ArrayList<QuestionObject> getExtraQuestions() {
        return questionsList;
    }


    public boolean hasData() {
        if (dataFile.length() > 0) {
            return true;
        }
        return false;
    }


    //Returns the current direction (Push of Pull)
    public int getDirection() {
        return current.getDirection();
    }


    public File getParticipantsFile() {
        return participantsFile;
    }

    public File getDataFile() {
        return dataFile;
    }

//-------------------- Measure the data and write them to file when finished ---------------

    //Start a new measure for every new image.
    public void startMeasure() {
        newMeasure.newMeasure(run, current.toString(), current.getDirection(), current.getType()); //Begin met de metingen opslaan.
        startMeasure = System.currentTimeMillis();  //Begintijd
    }

    //Get measurement. Measurement is in milliseconds
    public long getMeasurement() {
        return System.currentTimeMillis() - startMeasure;
    }

    /*
   This method adds data to the Participants Data table. This data consists of at least the ID number. Next to the the
   answers to the optional questions from the configuration files will be added to it.
    */
    private void addParticipantsData() {
        ArrayList<String> columnNames = new ArrayList<String>();

        columnNames.add("ID"); //The Participant always has an ID
        if (extraQuestions != null) {
            for (String key : extraQuestions.keySet()) {
                columnNames.add(key);
            }
        }
        dynamic.setColumnNames(columnNames);         //Set the column headers for the table Data
        ArrayList<Object> results = new ArrayList<Object>();
        results.add(id);

        if (extraQuestions != null) {
            for (String key : extraQuestions.keySet()) {
                results.add(extraQuestions.get(key));
            }
        }
        dynamic.add(results);
        dynamic.display();
    }


    /*
    This methods writes data to 2 seperate files. One for the data from the participant and the other
    one are the results. Data is appended to an existing file if there is any.
     */
    private void writeToFile() {
        CSVWriter writer = new CSVWriter(newMeasure.getAllResults());
        CSVWriter writer2 = new CSVWriter(this.dynamic);
        writer.writeData(dataFile);
        writer2.writeData(participantsFile);
    }

//------------------------------Get results from the test -------------------------------

    //Display results and write them to a file
    public TableModel getResults() {

        return newMeasure.getAllResults();
        //   return newMeasure.getAllResults();
    }

    //Fetches the results voor the latest participant.
    public HashMap<String, float[]> getResultsPerCondition() {
        HashMap<String, float[]> results = new HashMap<String, float[]>();
        results.put("Pull & Neutral", convertToArray(newMeasure.getMeasures(AATImage.PULL, AATImage.NEUTRAL)));
        results.put("Pull & Affective", convertToArray(newMeasure.getMeasures(AATImage.PULL, AATImage.AFFECTIVE)));
        results.put("Push & Neutral", convertToArray(newMeasure.getMeasures(AATImage.PUSH, AATImage.NEUTRAL)));
        results.put("Push & Affective", convertToArray(newMeasure.getMeasures(AATImage.PUSH, AATImage.AFFECTIVE)));
        return results;
    }


    private float[] convertToArray(ArrayList<Long> input) {
        float[] array = new float[input.size()]; //Create a new array
        for (int x = 0; x < input.size(); x++) {
            long l = input.get(x);
            array[x] = (float) l;
        }
        return array;
    }

    //--------------Input from the joystick controller-------------------------------------//


    /*
        Gets changes in the movement from the y-axis of the joystick.
        Test status has to be changed when the joystick reaches the maximum distance. It depends on the direction that belongs to
        the current image. When the user performs the requested action, when finished the test status has to go to wait for trigger.
     */
    public void changeYaxis(int value) {
        if (value != resize) {
            resize = value;
            if (testStatus == AATModel.IMAGE_LOADED || testStatus == AATModel.PRACTICE_IMAGE_LOADED) { //Only listen when there is an image loaded
                newMeasure.addResult(resize, getMeasurement());     //add results to the other measurements
                if (current.getDirection() == AATImage.PULL && value == 1) {   //check if the requested action has been performed
                    removeImage();
                } else if (current.getDirection() == AATImage.PUSH && value == getStepRate()) {
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
        if (testStatus == AATModel.TEST_SHOW_RESULTS) {
            testStatus = AATModel.TEST_STOPPED;
            this.setChanged();
            this.notifyObservers("Finished");
            return;
        }

        if (testStatus == AATModel.TEST_SHOW_FINISHED) {
            testStatus = AATModel.TEST_SHOW_RESULTS;
            this.setChanged();
            this.notifyObservers("Display results");
            return;
        }

        if (testStatus == AATModel.TEST_WAIT_FOR_TRIGGER) {
            if (practice) {
                testStatus = AATModel.PRACTICE_IMAGE_LOADED;
            } else {
                testStatus = AATModel.IMAGE_LOADED;
            }
            NextStep();    //Determine next step in the test.
            return;
        }
        if (testStatus == AATModel.TEST_STOPPED) {
            testStatus = AATModel.TEST_SHOW_FINISHED;
            this.setChanged();
            this.notifyObservers("Test ended");
            return;

        }
    }

//--------------------------User input, answers to the questions ----------------------------------------

    //Create a list with the answers to the optional questions.
    public void addExtraQuestions(HashMap<String, String> extraQuestions) {
        this.extraQuestions = extraQuestions;
        this.addParticipantsData();
        this.setChanged();
        this.notifyObservers("Start");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Test status is wait for the user to press the trigger button.
    }
}