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


import AAT.Util.FileUtils;
import AAT.validation.FalseConfigException;
import DataStructures.TestConfiguration;
import IO.ConfigFileReader;
import DataStructures.AATImage;
import DataStructures.Questionnaire;
import IO.XMLReader;

import java.io.*;
import java.util.ArrayList;
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
    public ConfigFileReader configFileReader;
    //   private TextReader xmlReader;
    private XMLReader xmlReader;
    //TODO: renamen

    private int test_id = 1;

    //Test variables
    private int repeat;
    private int breakAfter;
    public int practiceRepeat;
    public int n_pushPerc = 50;
    public int a_pushPerc = 50;
    public int trialSize = 0;
    public int affectPerc = 50;
    private int imageSizePerc = 50;
    private int maxSizePerc = 100;
    private int dataSteps = 9; //Default value 9
    private int stepSize = 31;
    private String displayQuestions;
    private boolean showBoxPlot;
    private Questionnaire questionnaire;
    private String plotType = "";

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
    public ArrayList<File> practiceImages;
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.

    public File practiceDir,affectiveDir,neutralDir;

    private String pullTag = "pull", pushTag = "push";
    private String nDir, aDir, pDir;

    //Hashmaps containing color information and the optional questions
    private Hashtable<Integer, String> colorTable;    //Contains the border colors
//    private ArrayList<QuestionData> questionsList;

    private File dataFile;




    private static final String HEX_PATTERN = "(^[0-9A-F]+$)";



    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     *
     * @param config The config file
     * @throws FalseConfigException When there are mistakes in the config file
     */
    public static TestConfiguration ValidateTestConfig(File config) throws FalseConfigException {

        TestConfiguration testConfiguration = new TestConfiguration();
        practiceImages = new ArrayList<File>();
        Pattern hexPattern = Pattern.compile(HEX_PATTERN);
        configFileReader = new ConfigFileReader(config);
        String workingDir = config.getParentFile().getAbsolutePath();

        System.out.println("Checking configuration");
        if(configFileReader.getValue("ID").length()>0) {
        try {

        test_id = Integer.parseInt(configFileReader.getValue("ID"));
            System.out.println("Test ID "+test_id);
        }
        catch (Exception e)   {

            throw new FalseConfigException("Test id value is not a correct integer value.");
        }
        }
        else {          //Add a new id value to the config file.
            test_id = 1;
            Writer output;
            try {
                FileWriter fw = null;
                try {
                    fw = new FileWriter(config,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintWriter pw = new PrintWriter(fw);

            pw.write("\n# Unique ID value. This value is used to determine whether this file has changed since the last time the test was taken.\n");
            pw.write("ID 0\n");
                pw.flush();
                pw.close();
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
            }
            catch (Exception e) {
                System.out.println("Could not write to config file.");
            }
        }

        String datFile = configFileReader.getValue("DataFile");
        //TODO uitzoeken wat de teststatus is. Indien nodig moet de datafile verandert worden en de oudere gebackuped worden.
        if (datFile.equals("")) {
            datFile = "Data.xml"; //Set to default.
        }

        dataFile = new File(workingDir + File.separator + datFile);
        String langFile = configFileReader.getValue("LanguageFile");
        File languageFile = new File(workingDir + File.separator + langFile);
        if (langFile.equals("") || !languageFile.exists() || languageFile.isDirectory()) {
            throw new FalseConfigException("No language file specified");
        }
        System.out.println("Language file = " + langFile);
        xmlReader = new XMLReader(languageFile);
        System.out.println("Language file = " + langFile);

        //TODO
        displayQuestions = configFileReader.getValue("DisplayQuestions");
        if (!displayQuestions.equals("Before") && !displayQuestions.equals("After") && !displayQuestions.equals("None")) {
            throw new FalseConfigException("DisplayQuestions should be either Before, After or None");
        }
        if (!displayQuestions.equals("None")) {   //Test has a Questionnaire
            System.out.println("Test has a Questionnaire");
            hasQuestions = true;
            String qFile = configFileReader.getValue("Questionnaire");
            System.out.println("Questionnaire is set to " + qFile);
            File questionFile = new File(workingDir + File.separator + qFile);
            if (qFile.length() == 0) {
                throw new FalseConfigException("The specified Questionnaire doesn't exist");
            }
            if (questionFile.exists() && !questionFile.isDirectory()) {
                hasQuestions = true;
                xmlReader.addQuestionnaire(questionFile);
            } else if (!questionFile.exists() && !(qFile.length() > 0)) {
                throw new FalseConfigException("The specified Questionnaire doesn't exist");
            }
        } else {
            hasQuestions = false;
        }
        // id = getHighestID();
        nDir = configFileReader.getValue("NeutralDir");
        aDir = configFileReader.getValue("AffectiveDir");
        pDir = configFileReader.getValue("PracticeDir");
        neutralDir = new File(workingDir +File.separator+ nDir);
        System.out.println(neutralDir);
        affectiveDir = new File(workingDir +File.separator+ aDir);
        File prDir = new File(workingDir + File.separator+ pDir);
        if (nDir.equals("") || !neutralDir.isDirectory()) {
            throw new FalseConfigException("Directory for the neutral images is not set properly");
        }
        if (aDir.equals("") || !affectiveDir.isDirectory()) {
            throw new FalseConfigException("Directory for the affective images is not set properly");
        }
        if(pDir.length()>0 && !prDir.isDirectory()) {
            throw new FalseConfigException("Directory for the practice images is not set properly");
        }
        if (!imageComplete(neutralDir,affectiveDir)) {
            throw new FalseConfigException("Some of the configured images are not present");
        }
        System.out.println("Neutral directory = " + neutralDir);
        System.out.println("Affective directory = " + affectiveDir);
        //    xmlReader = new TextReader(languageFile);

       // neutralImages = getImages(neutralDir);

        neutralImages = xmlReader.getIncludedFilesF(neutralDir);

        System.out.println("Neutral " + configFileReader.getValue("NeutralDir") + " " + neutralDir);
        if (neutralImages.size() == 0) {
            throw new FalseConfigException("Neutral images directory contains no images");
        }
    //    affectiveImages = getImages(affectiveDir);
        affectiveImages = xmlReader.getIncludedFilesF(affectiveDir);
        if (affectiveImages.size() == 0) {
            throw new FalseConfigException("Affective images directory contains no images");
        }
        if (hasQuestions) {
            questionnaire = new Questionnaire(xmlReader.getExtraQuestions(), xmlReader.getQuestionnaireIntro());
        }
        String doBorders = configFileReader.getValue("ColoredBorders");
        if (!doBorders.equals("True") && !doBorders.equals("False")) {
            throw new FalseConfigException("ColoredBorders has a false value, must be True or False");
        }
        coloredBorders = doBorders.equals("True");

        if (configFileReader.getValue("ColoredBorders").equals("True")) {
            System.out.println("Colored borders is set to True");
            colorTable = new Hashtable<Integer, String>();
            String pullColor = configFileReader.getValue("BorderColorPull");
            Matcher matcher = hexPattern.matcher(pullColor);
            if (!(pullColor.length() == 6) || !matcher.matches()) {
                throw new FalseConfigException("The color specified for the pull border is not a valid 6 character hex value");
            }

            String pushColor = configFileReader.getValue("BorderColorPush");
            matcher = hexPattern.matcher(pushColor);
            if (!(pushColor.length() == 6) || !matcher.matches()) {
                throw new FalseConfigException("The color specified for the push border is not a valid 6 character hex value");
            }
            //   colorTable.put(AATImage.PULL, "FF" + pullColor);
            //   colorTable.put(AATImage.PUSH, "FF" + pushColor);  //Also add alpha channel for processing
            colorTable.put(AATImage.PULL, pullColor);
            colorTable.put(AATImage.PUSH, pushColor);
            try {
                borderWidth = Integer.parseInt(configFileReader.getValue("BorderWidth"));
            } catch (Exception e) {
                throw new FalseConfigException("Border width is not configured properly");
            }
            System.out.println("Border push color is: " + pushColor);
            System.out.println("Border pull color is: " + pullColor);
        } else {       //Check for pull and push tag
            System.out.println("Colored borders is set to False:");
            pullTag = configFileReader.getValue("PullTag");
            pushTag = configFileReader.getValue("PushTag");
            if (pullTag.length() == 0) {
                throw new FalseConfigException("Pull tag is not set");
            }
            if (pushTag.length() == 0) {
                throw new FalseConfigException("Push tag is not set");
            }     //Check for practice dir
            System.out.println("Pull tag is set to " + pullTag);
            System.out.println("Push tag is set to " + pushTag);
            if (practiceRepeat > 0) {
                System.out.println("Practice repeat >0");
                practice = true;
                String practDir = configFileReader.getValue("PracticeDir");
                if (practDir.equals("")) {
                    throw new FalseConfigException("When ColoredBorders is set to false, PracticeDir has to be defined");
                } else {
                    practiceDir = new File(practDir);
                    System.out.println("Practice dir: "+practDir);
                    if (!practiceDir.isDirectory()) {
                        throw new FalseConfigException("The directory for the practice images is nog properly configured");
                    }
                    practiceImages = xmlReader.getIncludedFilesF(practiceDir);
                }
            }
            //TODO:
        }
        try {

            repeat = Integer.parseInt(configFileReader.getValue("Trials"));
        } catch (Exception e) {
            throw new FalseConfigException("Number of trials is not configured properly");
        }
        System.out.println("Number of Trials is"  + configFileReader.getValue("Trials"));
        try {
            breakAfter = Integer.parseInt(configFileReader.getValue("BreakAfter"));
        } catch (Exception e) {
            throw new FalseConfigException("BreakAfter is not configured properly");
        }
        System.out.println("There will be a break after " + breakAfter + " trials");
        if (!configFileReader.getValue("StepSize").equals("")) {
            try {
                stepSize = Integer.parseInt(configFileReader.getValue("StepSize"));
            } catch (Exception e) {
                throw new FalseConfigException("StepSize is not configured properly");
            }
            if (stepSize % 2 == 0) {
                throw new FalseConfigException("Stepsize should be and odd number");
            }
        }
        if (!configFileReader.getValue("DataSteps").equals("")) {
            try {
                dataSteps = Integer.parseInt(configFileReader.getValue("DataSteps"));
            } catch (Exception e) {
                throw new FalseConfigException("DataSteps is not configured properly");
            }
            if (dataSteps % 2 == 0) {
                throw new FalseConfigException("DataSteps should be and odd number");
            }
        }

        if (!configFileReader.getValue("PracticeRepeat").equals("")) {  //When a value for practice repeat is set, check validity
            try {

                practiceRepeat = Integer.parseInt(configFileReader.getValue("PracticeRepeat"));
            } catch (Exception e) {
                throw new FalseConfigException("PracticeRepeat is not configured properly");
            }
            if (practiceRepeat > 0) {
                practice = true;
                System.out.println("Test has practice");
                String practDir = configFileReader.getValue("PracticeDir");

                if (practDir.equals("")) {
                    System.out.println("Practice dir is not defined");
                    if (coloredBorders) {
                        System.out.println("Practice with colored borders");
                        practiceFillColor = configFileReader.getValue("PracticeFillColor");
                        Matcher matcher;
                        hexPattern.matcher(practiceFillColor);
                        matcher = hexPattern.matcher(practiceFillColor);
                        if (!(practiceFillColor.length() == 6) || !matcher.matches()) {
                            throw new FalseConfigException("The color specified for the practice image fill color is not a valid 6 character hex value\n" +
                                    "Or you forgot to set the directory containing the practice images");
                        }
                    } else {
                        throw new FalseConfigException("When practiceDir isn't set, ColoredBorder has to be set to True \n" +
                                "Or you forgot to set the directory containing the practice images");
                    }
                } else {
                    System.out.println("Practice without colored borders");
                    practiceDir = new File(workingDir +File.separator + practDir);
                    System.out.println("Practice dir is set to " + practiceDir.getAbsoluteFile());
                    if (!practiceDir.isDirectory()) {
                        throw new FalseConfigException("The directory for the practice images is nog properly configured");
                    }
                    practiceImages = xmlReader.getIncludedFilesF(practiceDir);

                }
                testList = createRandomPracticeList(); //TODO: Can be done nicer
                if (testList.size() == 0) {
                    throw new FalseConfigException("Practice images directory contains no images");
                }
                testList = null; //Remove the test list from memory
                practice = true;
            }


        }

        String hasBoxplot = configFileReader.getValue("ShowBoxPlot");
        if (hasBoxplot.equals("True")) {
            showBoxPlot = true;
        } else if (hasBoxplot.equals("False")) {
            showBoxPlot = false;
        } else {
            throw new FalseConfigException("ShowBoxPlot should be either True or False");
        }

        plotType = configFileReader.getPlotType(configFileReader.getValue("PlotType"));

        if (!configFileReader.getValue("AffectRatio").equals("")) {
            a_pushPerc = getPercentage(configFileReader.getValue("AffectRatio"), "AffectRatio");
        }
        if (!configFileReader.getValue("NeutralRatio").equals("")) {
            n_pushPerc = getPercentage(configFileReader.getValue("NeutralRatio"), "NeutralRatio");
        }
        if (!configFileReader.getValue("TestRatio").equals("")) {
            affectPerc = getPercentage(configFileReader.getValue("TestRatio"), "TestRatio");
        }
        if (!configFileReader.getValue("TrialSize").equals("")) {
            try {
                trialSize = Integer.parseInt(configFileReader.getValue("TrialSize"));
            } catch (Exception e) {
                throw new FalseConfigException("TrialSize is not set to a correct number");
            }
        }

        if (!configFileReader.getValue("MaxSizePerc").equals("")) {
            try {
                maxSizePerc = Integer.parseInt(configFileReader.getValue("MaxSizePerc"));
            } catch (Exception e) {
                throw new FalseConfigException("Maximum image size is not a number");
            }
            if (maxSizePerc <= 0) {
                throw new FalseConfigException("Maximum image size should be larger than 0");
            }
        }
        if (!configFileReader.getValue("ImageSizePerc").equals("")) {
            try {
                imageSizePerc = Integer.parseInt(configFileReader.getValue("ImageSizePerc"));
            } catch (Exception e) {
                throw new FalseConfigException("Image start size percentage is not a number");
            }
            if (imageSizePerc <= 0) {
                throw new FalseConfigException("Image start size percentage should be larger than 0");
            }
        }
        if (breakAfter == repeat) {
            throw new FalseConfigException("Number of trials and Break After values cannot be the same");
        }
    }








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

    public String getPlotType() {
        return plotType;
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

    public boolean imageComplete(File neutral, File affective) {
        int aCount = 0;
        int nCount = 0;

        ArrayList<File> aDisk = FileUtils.getImages(affective);
        ArrayList<File> nDisk = FileUtils.getImages(neutral);
        ArrayList<String> aFiles = xmlReader.getIncludedFiles(affective);
        ArrayList<String> nFiles = xmlReader.getIncludedFiles(neutral);
        for(File f : aDisk) {
            System.out.println("Looking for "+f.getName());
            if(aFiles.contains(f.getName()))  {
                aCount++;
            }

        }
        for(File f : nDisk) {
            System.out.println("Looking for "+f.getName());
            if(nFiles.contains(f.getName()))  {
                nCount++;
            }

        System.out.println("Counted A "+aCount+" disk "+aFiles.size()+"Counted N "+nCount+" disk "+nFiles.size());
        }
        if(nCount == nFiles.size() && aCount == aFiles.size())  {
            return true;
        }

        return false;
    }

    public int getTest_id() {
        return test_id;
    }

    public String getType(int i) {
        if (i == AATImage.AFFECTIVE) {
            File f = new File(aDir);
            return f.getName();
        } else if (i == AATImage.NEUTRAL) {
            File f = new File(nDir);
            return f.getName();
        } else {
            return "practice";
        }
    }

    public int getMaxSizePerc() {
        return maxSizePerc;
    }

    public int getImageSizePerc() {
        return imageSizePerc;
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

