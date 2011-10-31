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
 * <p/>
 * TODO: Iets doen met de verschillende opties van weergave.
 * TODO: Misschien een testplaatje als eerste
 * TODO: Nog eerst een testrun toevoegen.
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
    private static int TEST_WAIT_FOR_QUESTIONS = 3;
    private static int TEST_SHOW_FINISHED = 5;
    private static int TEST_SHOW_RESULTS = 4;

    private Hashtable<String, String> testOptions;


    private int resize = 5;     //Start with the middle of the joystick


    //Test variables
    private int repeat;
    private int breakAfter;
    private int count; //Counts the number of images shown.
    private int run;
    private int id = 0;

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
    private HashMap<String, String> extraQuestions;
    private DynamicTableModel dynamic;

    private File languageFile;
    private File participantsFile;
    private File dataFile;


    private Pattern pattern;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";        //regex for extension filtering


    //Constructor.
    public AATModel(File config) {
        dynamic = new DynamicTableModel();
        testConfig = new TestConfig(config);
        participantsFile = new File(testConfig.getValue("ParticipantsFile"));
        dataFile = new File(testConfig.getValue("DataFile"));
        id = getHighestID(participantsFile);
        File neutralDir = new File("images" + File.separator + testConfig.getValue("AffectiveDir"));
        File affectiveDir = new File("images" + File.separator + testConfig.getValue("NeutralDir"));
        languageFile = new File(testConfig.getValue("LanguageFile"));
        textReader = new TextReader(languageFile);
        pattern = Pattern.compile(IMAGE_PATTERN);
        neutralImages = getImages(neutralDir);
        affectiveImages = getImages(affectiveDir);

        testStatus = AATModel.TEST_STOPPED;
        if (testConfig.getValue("ColoredBorders").equals("True")) {
            colorTable = new Hashtable<Integer, String>();
            colorTable.put(AATImage.PULL, testConfig.getValue("BorderColorPull"));
            colorTable.put(AATImage.PUSH, testConfig.getValue("BorderColorPush"));
        }
        DataExporter dataExporter = new DataExporter(new File("export.csv"), participantsFile, dataFile, this);
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
        id++;          //new higher id
        newMeasure = new MeasureData(id);
        this.setChanged();
        if (textReader.getExtraQuestions().size() > 0) {   //When there are extra question, show them
            testStatus = AATModel.TEST_WAIT_FOR_QUESTIONS;
            notifyObservers("Show questions");      //Notify the observer that a new test is started.
        } else {
            testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Test status is wait for the user to press the trigger button.
            notifyObservers("Start");
        }

        //TODO: Create more states for the test. Show the question Frame, Practice trails
    }

    //Which view is enabled
    public int getCurrentView() {
        int currentView;
        currentView = 0;
        return currentView;
    }

    /*
        This method determines the next step to be taken in the test.
        TODO: Nog uitbreiden met de andere states

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
                System.out.println("Einde van de test");
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


    //Return the total number of images a participant gets shown
    public int getTotalImageCount() {
        int affective = affectiveImages.size();
        int neutral = neutralImages.size();
        int runCount = Integer.parseInt(testConfig.getValue("Trails"));
        return 2 * (affective + neutral) * runCount;
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

    //Read the optional extra questions from the language file specified in the configuration file.
    public ArrayList<QuestionObject> getExtraQuestions() {
        return textReader.getExtraQuestions();
    }


    //Create a list with the answers to the optional questions.
    public void addExtraQuestions(HashMap<String, String> extraQuestions) {
        this.extraQuestions = extraQuestions;
        this.addParticipantsData();
        System.out.println("Extra questions added");
        this.setChanged();
        this.notifyObservers("Start");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Test status is wait for the user to press the trigger button.
        System.out.println("Test status " + testStatus);

    }


    /*
   This method adds data to the Participants Data table. This data consists of at least the ID number. Next to the the
   answers to the optional questions from the configuration files will be added to it.
    */
    private void addParticipantsData() {
        ArrayList<String> columnNames = new ArrayList<String>();

        columnNames.add("ID"); //The Participant always has an ID
        int x = 1;
        for (String key : extraQuestions.keySet()) {
            columnNames.add(key);
            x++;
        }
        dynamic.setColumnNames(columnNames);         //Set the column headers for the table Data
        ArrayList<Object> results = new ArrayList<Object>();
        results.add(id);
        int i = 1;

        for (String key : extraQuestions.keySet()) {
            results.add(extraQuestions.get(key));
            i++;
        }
        dynamic.add(results);
        dynamic.display();
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

    //Write results to file. The Measured data and Participants data go to seperate files
    private void writeToFile() {
        CSVWriter writer = new CSVWriter(newMeasure.getAllResults());
        CSVWriter writer2 = new CSVWriter(this.dynamic);
        writer.writeData(dataFile);
        writer2.writeData(participantsFile);
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
                if (current.getDirection() == AATImage.PULL && value == getStepRate()) {   //check if the requested action has been performed
                    removeImage();
                } else if (current.getDirection() == AATImage.PUSH && value == 1) {
                    removeImage();
                }
            }
            this.setChanged();
            notifyObservers("Y-as");
        }
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

    //Remove image when the user has performed the action. Set test status to wait for trigger
    private void removeImage() {
        this.setChanged();
        notifyObservers("Wait screen");
        testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
    }

    /*When the test is waiting for the trigger, check if the trigger is pressed and then change the test status
    to image loaded. then call nextstep so the model can determine the appropriate next action to take
    */
    public void triggerPressed() {                   //TODO: verbeteren  Switch is mooier
             System.out.println("Trigger2 "+testStatus);
        if(testStatus == AATModel.TEST_SHOW_FINISHED) {
            testStatus = AATModel.TEST_SHOW_RESULTS;
            this.setChanged();
            this.notifyObservers("Display results");
        }

        if (testStatus == AATModel.TEST_WAIT_FOR_TRIGGER) {
            System.out.println("Trigger pressed");
            testStatus = AATModel.IMAGE_LOADED;
            NextStep();
        }
        if (testStatus == AATModel.TEST_STOPPED) {
            System.out.println("Hier is de test gestopt");
            testStatus = AATModel.TEST_SHOW_FINISHED;
            this.setChanged();
            this.notifyObservers("Test ended");

        }
    }
}


/*
This class reads the configuration file belonging to a AAT Test. All the important options like Directories, no of trials, Bordercolors etc.
can be set in this file
 */
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
            "PracticeTrails",
            "ParticipantsFile",
            "DataFile"

    };

    //Constructor, fills the Hashtable with the options
    public TestConfig(File testConfig) {
        this.testConfig = testConfig;
        testOptions = new Hashtable<String, String>();
        for (int x = 0; x < options.length; x++) {
            testOptions.put(options[x], "");

        }
        readConfig();
    }

    /*
    Reads the configuration file. Every time the reader discovers a key that is in the options list. It reads it's value from
    the configuration file and updates it's value in the HashMap
     */
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


/*
Reads from the language file specified in the test configuration. This makes it possible to use the same test in
different languages. This file can also contain optional questions that a researcher might be interested in.
 */
class TextReader {

    private Map<String, String> testText = new HashMap<String, String>();
    private ArrayList<QuestionObject> extraQuestions;

    private File languageFile;


    private ArrayList<String> questionKeys;

    //All the configuration options
    String[] options = {              //Keys defining the different texts
            "Introduction",
            "Start",
            "Break",
            "Finished"
    };


    public TextReader(File languageFile) {
        this.languageFile = languageFile;
        for (int x = 0; x < options.length; x++) {
            testText.put(options[x], "");
        }
        questionKeys = new ArrayList<String>();
        questionKeys.add("<Question>");         //Keys defining questions
        questionKeys.add("</Question>");
        questionKeys.add("<Option>");
        questionKeys.add("<Key>");
        extraQuestions = new ArrayList<QuestionObject>();
        readConfig();
    }

    //Remove < and > character from a string
    private String transform(String s) {
        s = s.replace("<", "");
        s = s.replace(">", "");
        return s;
    }

    /*
    This method reads the Language file defined in the config file. This file contains the different texts that are showed during
    the test. This file also contains optional extra questions that can be asked to the participant at the start of a new test.
    For example ask for gender or age.
     */
    private void readConfig() {
        String strLine;
        String key = "";
        String text = "";
        QuestionObject newQuestion = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(languageFile));
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    strLine = null;
                }
                assert strLine != null;
                if (strLine.startsWith(("<"))) {
                    if (!strLine.startsWith("</")) {
                        if (strLine.startsWith("<Question>")) {     //Line indicates that there is a question.
                            newQuestion = new QuestionObject();
                        } else if (testText.containsKey(transform(strLine))) {
                            key = transform(strLine);
                            text = "";
                        } else {
                            readQuestion(strLine, newQuestion);
                        }
                    } else {
                        if (strLine.startsWith("</Question")) {    //End of the question
                            extraQuestions.add(newQuestion);
                            newQuestion = new QuestionObject();
                        }
                    }
                } else {
                    if (testText.containsKey(key)) {
                        text += strLine + "\n";      //Read text lines
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
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    //Returns the requested value belonging to a key in the Hashmap
    public String getValue(String key) {
        return testText.get(key);
    }

    /*
    Input from the languagefiles is checked for lines that indicate there is an extra question
    When there is a <Question> a new question is available. The <Text> is followed by the question line
    <Option> is followed by an answer option. When there are no options given, then this question is a open question.
    <Key> is the name the variable for the answers will be given.
     */
    private void readQuestion(String line, QuestionObject question) {
        StringTokenizer st = new StringTokenizer(line, " ");
        String token = st.nextToken();
        String result = "";

        if (line.length() > token.length()) {
            result = line.substring(token.length() + 1);
        }
        if (token.startsWith("<Key")) {
            question.setKey(result);
        }
        if (token.startsWith("<Text>")) {
            question.setQuestion(result);
        }
        if (token.startsWith("<Option>")) {
            question.addOptions(result);
        }
    }

    public ArrayList<QuestionObject> getExtraQuestions() {
        return extraQuestions;
    }
}


/*
Data structure which contains a optional question from the configuration files. This can be used to show the question + (Answer options)
to the screen. The key String is used as a column header for use in a table or CSV file.
 */
class QuestionObject {

    private String key;
    private String question;
    private ArrayList<String> options;

    public QuestionObject() {
        options = new ArrayList<String>();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public String getKey() {
        return key;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}