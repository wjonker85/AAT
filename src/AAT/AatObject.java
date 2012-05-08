/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package AAT;


import Configuration.TestConfig;
import DataStructures.AATImage;
import DataStructures.Questionnaire;
import IO.XMLReader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 11/14/11
 * Time: 2:58 PM
 * This class sets up a new test. It starts with checking that all the configuration options are set properly and then
 * assigns the correct values to their corresponding variables. When asked this object can create a random list of images.
 * A practice list and the normal test list.
 */


public abstract class AatObject {

    //Configuration readers
    public TestConfig testConfig;
    //   private TextReader xmlReader;
    private XMLReader xmlReader;
    //TODO: renamen

    //Test variables
    private int repeat;
    private int breakAfter;
    public int practiceRepeat;
    public int n_pushPerc = 50;
    public int a_pushPerc = 50;
    public int trialSize = 0;
    public int affectPerc = 50;
    private int dataSteps = 9; //Default value 9
    private int stepSize = 31;
    private String displayQuestions;
    private boolean showBoxPlot;
    private Questionnaire questionnaire;

    //Test view variables
    private int borderWidth;

    public String practiceFillColor;
    private boolean coloredBorders;


    //progress variables
    private boolean practice;
    private boolean hasQuestions = false;

    //Different lists, containing the files
    public ArrayList<File> neutralImages;
    public ArrayList<File> affectiveImages;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.

    public File practiceDir;

    private String pullTag = "pull", pushTag = "push";
    private String nDir, aDir;

    //Hashmaps containing color information and the optional questions
    private Hashtable<Integer, String> colorTable;    //Contains the border colors
//    private ArrayList<QuestionData> questionsList;

    private File dataFile;


    //regex for extension filtering
    private Pattern pattern;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    private static final String HEX_PATTERN = "(^[0-9A-F]+$)";


    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     *
     * @param config The config file
     * @throws FalseConfigException When there are mistakes in the config file
     */
    public AatObject(File config) throws FalseConfigException {
        pattern = Pattern.compile(IMAGE_PATTERN); //create regex
        Pattern hexPattern = Pattern.compile(HEX_PATTERN);
        testConfig = new TestConfig(config);
        String workingDir = config.getParentFile().getAbsolutePath();

        System.out.println("Checking configuration");

        String datFile = testConfig.getValue("DataFile");
        if (datFile.equals("")) {
            datFile = "Data.xml"; //Set to default.
        }

        dataFile = new File(workingDir + File.separator + datFile);

        String langFile = testConfig.getValue("LanguageFile");
        File languageFile = new File(workingDir + File.separator + langFile);
        if (langFile.equals("") || !languageFile.exists()) {
            throw new FalseConfigException("No language file specified");
        }
        System.out.println("Language file = " + langFile);
        xmlReader = new XMLReader(languageFile);
        String qFile = testConfig.getValue("Questionnaire");
        File questionFile = new File(workingDir + File.separator + qFile);
        if (questionFile.exists()) {
            hasQuestions = true;
            xmlReader.addQuestionnaire(questionFile);
        } else if (!questionFile.exists() && !(qFile.length() > 0)) {
            throw new FalseConfigException("The specified questionnaire doesn't exist");
        }

        System.out.println("Language file = " + langFile);
        // id = getHighestID();
        nDir = testConfig.getValue("NeutralDir");
        aDir = testConfig.getValue("AffectiveDir");
        File neutralDir = new File(workingDir + File.separator + nDir);
        File affectiveDir = new File(workingDir + File.separator + aDir);
        if (nDir.equals("") || !neutralDir.isDirectory()) {
            throw new FalseConfigException("Directory for the neutral images is not set properly");
        }
        if (aDir.equals("") || !affectiveDir.isDirectory()) {
            throw new FalseConfigException("Directory for the affective images is not set properly");
        }

        System.out.println("Neutral directory = " + neutralDir);
        System.out.println("Affective directory = " + affectiveDir);
        //    xmlReader = new TextReader(languageFile);

        neutralImages = getImages(neutralDir);

        System.out.println("Neutral " + testConfig.getValue("NeutralDir") + " " + neutralDir);
        if (neutralImages.size() == 0) {
            throw new FalseConfigException("Neutral images directory contains no images");
        }
        affectiveImages = getImages(affectiveDir);
        if (affectiveImages.size() == 0) {
            throw new FalseConfigException("Affective images directory contains no images");
        }
        if (hasQuestions) {
            questionnaire = new Questionnaire(xmlReader.getExtraQuestions(), xmlReader.getQuestionnaireIntro());
        }
        String doBorders = testConfig.getValue("ColoredBorders");
        if (!doBorders.equals("True") && !doBorders.equals("False")) {
            throw new FalseConfigException("ColoredBorders has a false value, must be True or False");
        }
        coloredBorders = doBorders.equals("True");

        if (testConfig.getValue("ColoredBorders").equals("True")) {
            System.out.println("Colored borders is set to True");
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
            //   colorTable.put(AATImage.PULL, "FF" + pullColor);
            //   colorTable.put(AATImage.PUSH, "FF" + pushColor);  //Also add alpha channel for processing
            colorTable.put(AATImage.PULL, pullColor);
            colorTable.put(AATImage.PUSH, pushColor);
            try {
                borderWidth = Integer.parseInt(testConfig.getValue("BorderWidth"));
            } catch (Exception e) {
                throw new FalseConfigException("Border width is not configured properly");
            }
            System.out.println("Border push color is: " + pushColor);
            System.out.println("Border pull color is: " + pullColor);
        } else {       //Check for pull and push tag
            System.out.println("Colored borders is set to False:");
            pullTag = testConfig.getValue("PullTag");
            pushTag = testConfig.getValue("PushTag");
            if (pullTag.length() == 0) {
                throw new FalseConfigException("Pull tag is not set");
            }
            if (pushTag.length() == 0) {
                throw new FalseConfigException("Push tag is not set");
            }     //Check for practice dir
            System.out.println("Pull tag is set to " + pullTag);
            System.out.println("Push tag is set to " + pushTag);
            if (practiceRepeat > 0) {
                practice = true;
                String practDir = testConfig.getValue("PracticeDir");
                if (practDir.equals("")) {
                    throw new FalseConfigException("When ColoredBorders is set to false, PracticeDir has to be defined");
                } else {
                    practiceDir = new File(practDir);
                    if (!practiceDir.isDirectory()) {
                        throw new FalseConfigException("The directory for the practice images is nog properly configured");
                    }

                }
            }
            //TODO: 
        }
        try {

            repeat = Integer.parseInt(testConfig.getValue("Trials"));
        } catch (Exception e) {
            throw new FalseConfigException("Number of trials is not configured properly");
        }
        System.out.println("Number of Trials is" + testConfig.getValue("Trials"));
        try {
            breakAfter = Integer.parseInt(testConfig.getValue("BreakAfter"));
        } catch (Exception e) {
            throw new FalseConfigException("BreakAfter is not configured properly");
        }
        System.out.println("There will be a break after " + breakAfter + " trials");
        if (!testConfig.getValue("StepSize").equals("")) {
            try {
                stepSize = Integer.parseInt(testConfig.getValue("StepSize"));
            } catch (Exception e) {
                throw new FalseConfigException("StepSize is not configured properly");
            }
            if (stepSize % 2 == 0) {
                throw new FalseConfigException("Stepsize should be and odd number");
            }
        }
        if (!testConfig.getValue("DataSteps").equals("")) {
            try {
                dataSteps = Integer.parseInt(testConfig.getValue("DataSteps"));
            } catch (Exception e) {
                throw new FalseConfigException("DataSteps is not configured properly");
            }
            if (dataSteps % 2 == 0) {
                throw new FalseConfigException("DataSteps should be and odd number");
            }
        }

        if (!testConfig.getValue("PracticeRepeat").equals("")) {  //When a value for practice repeat is set, check validity
            try {

                practiceRepeat = Integer.parseInt(testConfig.getValue("PracticeRepeat"));
            } catch (Exception e) {
                throw new FalseConfigException("PracticeRepeat is not configured properly");
            }
            if (practiceRepeat > 0) {
                practice = true;
                System.out.println("Test has practice");
                String practDir = testConfig.getValue("PracticeDir");

                if (practDir.equals("")) {
                    System.out.println("Practice dir is not defined");
                    if (coloredBorders) {
                        System.out.println("Practice with colored borders");
                        practiceFillColor = testConfig.getValue("PracticeFillColor");
                        Matcher matcher;
                        hexPattern.matcher(practiceFillColor);
                        matcher = hexPattern.matcher(practiceFillColor);
                        if (!(practiceFillColor.length() == 6) || !matcher.matches()) {
                            throw new FalseConfigException("The color specified for the practice image fill color is not a valid 6 character hex value");
                        }
                    } else {
                        throw new FalseConfigException("When practiceDir isn't set, ColoredBorder has to be set to True");
                    }
                } else {
                    System.out.println("Practice without colored borders");
                    practiceDir = new File(workingDir + File.separator + practDir);
                    if (!practiceDir.isDirectory()) {
                        throw new FalseConfigException("The directory for the practice images is nog properly configured");
                    }
                    System.out.println("Practice dir is set to " + practiceDir);
                }
                testList = createRandomPracticeList(); //TODO: Can be done nicer
                if (testList.size() == 0) {
                    throw new FalseConfigException("Practice images directory contains no images");
                }
                testList = null; //Remove the test list from memory
                practice = true;
            }


        }
        displayQuestions = testConfig.getValue("DisplayQuestions");
        if (!displayQuestions.equals("Before") && !displayQuestions.equals("After") && !displayQuestions.equals("None")) {
            throw new FalseConfigException("DisplayQuestions should be either Before, After or None");
        }
        String hasBoxplot = testConfig.getValue("ShowBoxPlot");
        if (hasBoxplot.equals("True")) {
            showBoxPlot = true;
        } else if (hasBoxplot.equals("False")) {
            showBoxPlot = false;
        } else {
            throw new FalseConfigException("ShowBoxPlot should be either True or False");
        }
        if (!testConfig.getValue("AffectRatio").equals("")) {         //TODO ratios veranderen
            a_pushPerc = getPercentage(testConfig.getValue("AffectRatio"), "AffectRatio");
        }
        if (!testConfig.getValue("NeutralRatio").equals("")) {
            n_pushPerc = getPercentage(testConfig.getValue("NeutralRatio"), "NeutralRatio");
        }
        if (!testConfig.getValue("TestRatio").equals("")) {
            affectPerc = getPercentage(testConfig.getValue("TestRatio"), "TestRatio");
        }
        if (!testConfig.getValue("TrialSize").equals("")) {
            try {
                trialSize = Integer.parseInt(testConfig.getValue("TrialSize"));
            } catch (Exception e) {
                throw new FalseConfigException("TrialSize is not set to a correct number");
            }
        }
        if (breakAfter == repeat) {
            throw new FalseConfigException("Number of trials and Break After values cannot be the same");
        }
    }


    /**
     * False configuration exception
     */
    public class FalseConfigException extends Exception {

        public FalseConfigException(String error) {
            super(error);
        }

    }

    public void setTempDataFile(File file) {
        dataFile = file;
    }

    private int getPercentage(String ratio, String s) throws FalseConfigException {
        if (!ratio.contains(":")) {
            throw new FalseConfigException(s + " is not a correct ratio");
        }

        String[] str = ratio.split(":");
        if (str.length != 2) {
            throw new FalseConfigException(s + " is not a correct ratio");
        }
        float first, second;
        try {
            first = Integer.parseInt(str[0]);
            second = Integer.parseInt(str[1]);
        } catch (Exception e) {
            throw new FalseConfigException(s + " is not a correct ratio");
        }
        if (first == 0) {
            return 0;
        }
        int total = (int) (first + second);
        return (int) ((first / total) * 100f);

    }

    /**
     * Loads all image files in a given directory. Extension filter with regular expression.
     *
     * @param dir Directory containing images
     * @return ArrayList<File> with all image files in a directory
     */
    public ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }


    /**
     * Filter so that only the image files in a directory will be selected
     */
    FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };


    /**
     * Images can have a border created by this program. This method returns that borders color.
     *
     * @param direction push or pull
     * @return The corresponding color
     */
    public String getBorderColor(int direction) {
        return colorTable.get(direction);
    }

    /**
     * Check whether a colored border has to de drawn around an image
     *
     * @return boolean, true for colored borders
     */
    public boolean hasColoredBorders() {
        return coloredBorders;
    }

    /**
     * When the test is on a break, a Break text is shown on the screen. This text comes from the languageFile
     *
     * @return Break text to be shown on the screen
     */
    public String getBreakText() {
        return xmlReader.getValue("break");
    }

    /**
     * When the test is started, a general introduction is shown on the screen. This text comes from the languageFile
     *
     * @return The introduction text.
     */
    public String getIntroductionText() {
        return xmlReader.getValue("introduction");
    }

    /**
     * This text is shown after the practice runs. Or when there are no practice runs, this text is the first text to
     * be shown.
     *
     * @return The start text
     */
    public String getTestStartText() {
        return xmlReader.getValue("start");
    }

    /**
     * The text to display when the test has ended
     *
     * @return Finished text
     */
    public String getTestFinishedText() {
        return xmlReader.getValue("finished");
    }

    /**
     * The stepRate determines in how many steps the the image is resized. The middle number is the middle position
     * of the joystick
     *
     * @return The steprate as specified in the configuration file.
     */
    public int getStepRate() {
        return stepSize;
    }

    /**
     * @return Width of the border around an image
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    /**
     * @return The file containing the data file
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * @return whether the data file contains data
     */

    public boolean hasData() {
        return dataFile.length() > 0;
    }


    public boolean hasBoxPlot() {
        return showBoxPlot;
    }


    /**
     * @return How many times the test has to be repeated
     */
    public int getRepeat() {
        return repeat;
    }

    /**
     * @return When the test needs a break
     */
    public int getBreakAfter() {
        return breakAfter;
    }

    public boolean hasPractice() {
        return practice;
    }

    public int getDataSteps() {
        return dataSteps;
    }

    public int centerPos() {
        return (dataSteps + 1) / 2;
    }

    public String getDisplayQuestions() {
        return displayQuestions;
    }

    public String getPullTag() {
        return pullTag;
    }

    public String getPushTag() {
        return pushTag;
    }

    public String getAffectiveDir() {
        return aDir;
    }

    public String getNeutralDir() {
        return nDir;
    }

    /**
     * Clear the image lists when they are not necessary anymore
     */
    public void clearLists() {
        this.testList = null;
        System.gc();

    }

    public String getType(int i) {
        if (i == AATImage.AFFECTIVE) {
            return aDir;
        } else if (i == AATImage.NEUTRAL) {
            return nDir;
        } else {
            return "practice";
        }
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    /**
     * @return A new randomised practice list
     */
    public abstract ArrayList<AATImage> createRandomPracticeList();


    /**
     * @return randomised list containing AATImages, that contains the image to be displayed and push/pull information.
     */
    public abstract ArrayList<AATImage> createRandomListBorders();

    public abstract ArrayList<AATImage> createRandomListNoBorders();
}

