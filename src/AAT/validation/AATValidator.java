package AAT.validation;

import IO.ConfigFileReader;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcel on 3/16/14.
 */
public class AATValidator {

    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     *
     * @param config The config file
     * @throws FalseConfigException When there are mistakes in the config file
     * <p/>
     */

    public static File workingDir;


    //Create a map containing all the test variables with validators attached to them.
    public static TestConfigurationMap<String> createValidatedConfigMap(File config) throws FalseConfigException {

        TestConfigurationMap<String> testConfigurationMap = new TestConfigurationMap<String>();
        ConfigFileReader configFileReader = new ConfigFileReader(config);
        //First set the working directory
        workingDir = new File(config.getParentFile().getAbsolutePath());
        testConfigurationMap.GetSetConfigOption("workingDir", workingDir);

        //Check for the ID value
        String idValue = "1";
        Boolean newID = true;
        if (configFileReader.getValue("ID").length() > 0) {
            idValue = configFileReader.getValue("ID");
            newID = false;
        }

        if (newID) {          //Add a new id value to the config file.
            addNewIDToConfigFile(config);
        }

        TestConfigurationOption<Integer> id = testConfigurationMap.GetSetConfigOption("testID", parseIntWithException("testID", configFileReader.getValue("ID")));

        //Specify the data file
        String datFile = configFileReader.getValue("DataFile");
        if (datFile.equals("")) {
            datFile = "Data.xml"; //Set to default.
        }
        testConfigurationMap.GetSetConfigOption("dataFile", datFile);


        //Set the basic properties of the test trialsize break etc.
        TestConfigurationOption<Integer> trials = testConfigurationMap.GetSetConfigOption("trials", parseIntWithException("trials", configFileReader.getValue("Trials")));
        trials.addValidator(new NumberValidator("trials", 0, trials.getValue() + 1, false));
        TestConfigurationOption<Integer> breakAfter = testConfigurationMap.GetSetConfigOption("breakAfter", parseIntWithException("breakAfter", configFileReader.getValue("BreakAfter")));
        breakAfter.addValidator(new NumberValidator("breakAfter", -2, trials.getValue(), false));

        //Check for a correct language file.
        TestConfigurationOption<File> lFile = testConfigurationMap.GetSetConfigOption("langFile", createFullPathFile(configFileReader.getValue("LanguageFile")));
        lFile.addValidator(new FileExistsValidator("Language"));

        //Check whether the test has a questionnaire and when applicable this questionnaire needs to be displayed.
        TestConfigurationOption<String> displayQuestions = testConfigurationMap.GetSetConfigOption("displayQuestions", configFileReader.getValue("DisplayQuestions"));
        displayQuestions.addValidator(new IConfigValidator<String>() {
            @Override
            public void validate(String s) throws FalseConfigException {
                if (!(s.equalsIgnoreCase("Before") || s.equalsIgnoreCase("Afters") || s.equalsIgnoreCase("None"))) {
                    throw new FalseConfigException("DisplayQuestions should be either Before, After or None");
                }
            }
        });
        TestConfigurationOption<File> questionnaireFile = testConfigurationMap.GetSetConfigOption("questionnaireFile", createFullPathFile(configFileReader.getValue("Questionnaire")));
        questionnaireFile.addValidator(new FileExistsValidator("Questionnaire"));

        //Set the directories containing the neutral and affective images.
        TestConfigurationOption<File> neutralDir = testConfigurationMap.GetSetConfigOption("neutralDir", createFullPathFile(configFileReader.getValue("NeutralDir")));
        neutralDir.addValidator(new ImageDirectoryValidator("Neutral"));
        TestConfigurationOption<File> affectiveDir = testConfigurationMap.GetSetConfigOption("affectiveDir", createFullPathFile(configFileReader.getValue("AffectiveDir")));
        affectiveDir.addValidator(new ImageDirectoryValidator("Affective"));

        //See whether the test has practice images and how often they should be repeated.
        TestConfigurationOption<Integer> practiceRepeat = testConfigurationMap.GetSetConfigOption("practiceRepeat", parseIntWithException("practiceRepeat", configFileReader.getValue("PracticeRepeat")));
        practiceRepeat.addValidator(new NumberValidator("PracticeRepeat", 0, -1, false));

        //See whether the AAT program should draw colored borders around the images or that the researcher has created it's own cue for the push or pull condition
        boolean doBorders = false;
        if (configFileReader.getValue("ColoredBorders").equalsIgnoreCase("true")) {
            doBorders = true;
        }
        if (configFileReader.getValue("ColoredBorders").equalsIgnoreCase("false")) {
            doBorders = false;
        }
        TestConfigurationOption<Boolean> coloredBorders = testConfigurationMap.GetSetConfigOption("coloredBorders", doBorders);
        if (coloredBorders.getValue()) {        //Test uses auto-generated colored borders
            TestConfigurationOption<String> pullColor = testConfigurationMap.GetSetConfigOption("borderColorPull", configFileReader.getValue("BorderColorPull"));
            pullColor.addValidator(new ColorValidator("pull"));
            TestConfigurationOption<String> pushColor = testConfigurationMap.GetSetConfigOption("borderColorPush", configFileReader.getValue("BorderColorPush"));
            pushColor.addValidator(new ColorValidator("push"));
            testConfigurationMap.GetSetConfigOption("borderWidth", parseIntWithException("BorderWidth", configFileReader.getValue("BorderWidth")));
        } else {
            //Researcher has specified it's own pull and push cues. Labels to define the push and pull images are necessary now.
            TestConfigurationOption<String> pullTag = testConfigurationMap.GetSetConfigOption("pullTag", configFileReader.getValue("PullTag"));
            pullTag.addValidator(new StringLengthValidator("pull tag"));
            TestConfigurationOption<String> pushTag = testConfigurationMap.GetSetConfigOption("pushTag", configFileReader.getValue("PushTag"));
            pushTag.addValidator(new StringLengthValidator("push tag"));

            if (practiceRepeat.getValue() > 0) {   //No border and a practice. Researcher has to supply the test with it's own practice images.
                TestConfigurationOption<File> practiceDir = testConfigurationMap.GetSetConfigOption("practiceDir", createFullPathFile(configFileReader.getValue("PracticeDir")));
                practiceDir.addValidator(new ImageDirectoryValidator("Practice"));
            }
        }

        if (practiceRepeat.getValue() > 0) {
            if (!testConfigurationMap.contains("practiceDir")) {
                if (coloredBorders.getValue()) {
                    System.out.println("Practice with colored borders");
                    TestConfigurationOption practiceFillColor = testConfigurationMap.GetSetConfigOption("practiceFillColor", configFileReader.getValue("PracticeFillColor"));
                    practiceFillColor.addValidator(new ColorValidator("Practice fill color"));
                } else {
                    throw new FalseConfigException("When practiceDir isn't set, ColoredBorder has to be set to True \n" +
                            "Or you forgot to set the directory containing the practice images");
                }
            }
        }

        String hasBoxplot = configFileReader.getValue("ShowBoxPlot");
        if (hasBoxplot.equalsIgnoreCase("True")) {
            testConfigurationMap.GetSetConfigOption("showBoxPlot", true);
        } else if (hasBoxplot.equalsIgnoreCase("False")) {
            testConfigurationMap.GetSetConfigOption("showBoxPlot", false);
        } else {
            throw new FalseConfigException("ShowBoxPlot should be either True or False");
        }


        //--------------------- Advanced options  ----------------------------------------------------------------------------------------
        testConfigurationMap.GetSetConfigOption("PlotType", configFileReader.getPlotType(configFileReader.getValue("PlotType")));
        if (!configFileReader.getValue("AffectRatio").equals("")) {
            TestConfigurationOption<Integer> a_pushPerc = testConfigurationMap.GetSetConfigOption("affectRatio", getPercentage(configFileReader.getValue("AffectRatio"), "AffectRatio"));
        }
        if (!configFileReader.getValue("NeutralRatio").equals("")) {
            TestConfigurationOption<Integer> n_pushPerc = testConfigurationMap.GetSetConfigOption("neutralRatio", getPercentage(configFileReader.getValue("NeutralRatio"), "NeutralRatio"));
        }
        if (!configFileReader.getValue("TestRatio").equals("")) {
            TestConfigurationOption<Integer> affectPerc = testConfigurationMap.GetSetConfigOption("testRatio", getPercentage(configFileReader.getValue("TestRatio"), "TestRatio"));
        }
        if (!configFileReader.getValue("TrialSize").equals("")) {
            TestConfigurationOption<Integer> trialSize = testConfigurationMap.GetSetConfigOption("trialSize", parseIntWithException("TrialSize", configFileReader.getValue("TrialSize")));
        }
        if (!configFileReader.getValue("MaxSizePerc").equals("")) {
            TestConfigurationOption<Integer> imageSizePerc = testConfigurationMap.GetSetConfigOption("imageSizePerc", parseIntWithException("ImageSizePerc", configFileReader.getValue("ImageSizePerc")));
        }

        //Set some performance options.
        TestConfigurationOption<Integer> stepSize = testConfigurationMap.GetSetConfigOption("stepSize", parseIntWithException("StepSize", configFileReader.getValue("StepSize")));
        stepSize.addValidator(new NumberValidator("StepSize", 0, 101, true));
        TestConfigurationOption<Integer> dataSteps = testConfigurationMap.GetSetConfigOption("dataSteps", parseIntWithException("DataSteps", configFileReader.getValue("DataSteps")));
        stepSize.addValidator(new NumberValidator("StepSize", 0, 101, true));

        return testConfigurationMap;
    }

    private static void addNewIDToConfigFile(File config) {
        Writer output;
        try {
            FileWriter fw = null;
            try {
                fw = new FileWriter(config, true);
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
        } catch (Exception e) {
            System.out.println("Could not write to config file.");
        }
    }


    private static int getPercentage(String ratio, String s) throws FalseConfigException {
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

    public static int parseIntWithException(String label, String value) throws FalseConfigException {
        int returnValue;
        try {
            returnValue = Integer.parseInt(value);
        } catch (Exception e) {
            throw new FalseConfigException("Value for " + label + " is not a number");
        }
        return returnValue;
    }

    public static File createFullPathFile(String file) {
        return new File(workingDir + File.separator + file);
    }
}

//----------------------------------Validators ----------------------------------------------------------------------
//Check whether a given directory is a valid directory.
class ImageDirectoryValidator implements IConfigValidator<File> {

    private String type;

    public ImageDirectoryValidator(String type) {
        this.type = type;
    }

    @Override
    public void validate(File dir) throws FalseConfigException {
        if (dir.getName().equals("") || !dir.isDirectory()) {
            throw new FalseConfigException("Directory for the " + type + " images is not set properly");
        }
    }
}

class StringLengthValidator implements IConfigValidator<String> {
    private String label;

    public StringLengthValidator(String label) {
        this.label = label;
    }

    @Override
    public void validate(String s) throws FalseConfigException {
        if (s.length() == 0) {
            throw new FalseConfigException(label + " is not set to a correct value");
        }
    }
}

class ColorValidator implements IConfigValidator<String> {

    private static final String HEX_PATTERN = "(^[0-9A-F]+$)";
    Pattern hexPattern;
    private String label;

    public ColorValidator(String label) {
        hexPattern = Pattern.compile(HEX_PATTERN);
        this.label = label;
    }

    @Override
    public void validate(String s) throws FalseConfigException {
        Matcher matcher = hexPattern.matcher(s);
        if (!(s.length() == 6) || !matcher.matches()) {
            throw new FalseConfigException("The color specified for the " + label + " border is not a valid 6 character hex value");
        }
    }


}

class NumberValidator implements IConfigValidator<Integer> {
    private String label;
    private int min, max;
    private boolean oddNumber;


    public NumberValidator(String label, int min, int max, boolean oddNumber) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.oddNumber = oddNumber;
    }

    @Override
    public void validate(Integer integer) throws FalseConfigException {
        int value = integer.intValue();
        if (max == -1) max = value + 1;
        if (value < min || value > max) {
            throw new FalseConfigException("Value " + value + " for property " + label + " is set either too large or too small.");
        }
        if (oddNumber) {
            if (value % 2 == 0) {
                throw new FalseConfigException("Value " + value + " for property " + label + " has to be an odd number");
            }
        }
    }
}

//Check whether a specified file exists
class FileExistsValidator implements IConfigValidator<File> {

    private String type;

    public FileExistsValidator(String type) {
        this.type = type;
    }

    @Override
    public void validate(File file) throws FalseConfigException {
        if (file.getName().length() == 0 || !file.exists() || file.isDirectory()) {
            throw new FalseConfigException(type + " is not configured properly.");
        }
    }
}