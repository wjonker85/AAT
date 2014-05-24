package AAT.Configuration.Validation;

import AAT.Configuration.TestConfiguration;
import IO.ConfigFileReader;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcel on 3/16/14.
 * This class  is used to read all the configuration options from the AAT config file and validate whether the structure of this file is correct
 * and all the values are correctly assigned. When no exception is thrown, the configuration is fine and the AAT can begin. When an exception is thrown, a user will receive
 * an information message telling them what is wrong.
 */
public class AATValidator {

    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     *
     *
     * @throws FalseConfigException When there are mistakes in the config file
     *
     */

    public static File workingDir;

    public static TestConfiguration validatedTestConfiguration(File config) {
        TestConfigurationMap<String> testConfigurationMap;
        try {
            testConfigurationMap = createValidatedConfigMap(config);
            testConfigurationMap.isValidated();
        } catch (FalseConfigException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Configuration Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return ConfigurationFiller.fillTestConfiguration(testConfigurationMap);
    }

    //Create a map containing all the test variables with validators attached to them.
    private static TestConfigurationMap<String> createValidatedConfigMap(File config) throws FalseConfigException {

        TestConfigurationMap<String> testConfigurationMap = new TestConfigurationMap<String>();
        ConfigFileReader configFileReader = new ConfigFileReader(config);
        //First set the working directory
        workingDir = new File(config.getParentFile().getAbsolutePath());
        testConfigurationMap.GetSetConfigOption("WorkingDir", workingDir);


        //Set these to default values. Can be left empty in the configuration file
        String defaultRatio = "1:1";
        testConfigurationMap.GetSetConfigOption("PracticeDir", new File(""));
        testConfigurationMap.GetSetConfigOption("PullTag", "pull");
        testConfigurationMap.GetSetConfigOption("PushTag", "push");
        testConfigurationMap.GetSetConfigOption("Questionnaire", new File(""));
        testConfigurationMap.GetSetConfigOption("AffectRatio", defaultRatio);
        testConfigurationMap.GetSetConfigOption("NeutralRatio", defaultRatio);
        testConfigurationMap.GetSetConfigOption("TestRatio", defaultRatio);
        testConfigurationMap.GetSetConfigOption("TrialSize", -1);
        testConfigurationMap.GetSetConfigOption("MaxSizePerc", 100);
        testConfigurationMap.GetSetConfigOption("ImageSizePerc", 50);

        //Check for the ID value
        Boolean newID = true;
        if (configFileReader.getValue("ID").length() > 0) {
            newID = false;
        }

        if (newID) {          //Add a new id value to the config file.
            addNewIDToConfigFile(config);
        }

        testConfigurationMap.GetSetConfigOption("ID", parseIntWithException("ID", configFileReader.getValue("ID")));

        //Specify the data file
        String datFile = configFileReader.getValue("DataFile");
        if (datFile.equals("")) {
            datFile = "Data.xml"; //Set to default.
        }
        testConfigurationMap.GetSetConfigOption("DataFile", createFullPathFile(datFile));


        //Set the basic properties of the test trialsize break etc.
        TestConfigurationOption<Integer> trials = testConfigurationMap.GetSetConfigOption("Trials", parseIntWithException("Trials", configFileReader.getValue("Trials")));
        trials.addValidator(new NumberValidator("Trials", 0, trials.getValue() + 1, false));
        TestConfigurationOption<Integer> breakAfter = testConfigurationMap.GetSetConfigOption("BreakAfter", parseIntWithException("BreakAfter", configFileReader.getValue("BreakAfter")));
        breakAfter.addValidator(new NumberValidator("BreakAfter", -2, trials.getValue(), false));

        //Check for a correct language file.
        TestConfigurationOption<File> lFile = testConfigurationMap.GetSetConfigOption("LanguageFile", createFullPathFile(configFileReader.getValue("LanguageFile")));
        lFile.addValidator(new FileExistsValidator("LanguageFile"));

        //Check whether the test has a questionnaire and when applicable this questionnaire needs to be displayed.
        TestConfigurationOption<String> displayQuestions = testConfigurationMap.GetSetConfigOption("DisplayQuestions", configFileReader.getValue("DisplayQuestions"));
        displayQuestions.addValidator(new IConfigValidator<String>() {
            @Override
            public void validate(String s) throws FalseConfigException {
                if (!(s.equalsIgnoreCase("Before") || s.equalsIgnoreCase("After") || s.equalsIgnoreCase("None"))) {
                    throw new FalseConfigException("DisplayQuestions should be either Before, After or None");
                }
            }
        });
        if (!displayQuestions.getValue().equals("None")) {
            TestConfigurationOption<File> questionnaireFile = testConfigurationMap.GetSetConfigOption("Questionnaire", createFullPathFile(configFileReader.getValue("Questionnaire")));
            questionnaireFile.addValidator(new FileExistsValidator("Questionnaire"));
        }
        //Set the directories containing the neutral and affective images.
        TestConfigurationOption<File> neutralDir = testConfigurationMap.GetSetConfigOption("NeutralDir", createFullPathFile(configFileReader.getValue("NeutralDir")));
        neutralDir.addValidator(new ImageDirectoryValidator("Neutral"));
        TestConfigurationOption<File> affectiveDir = testConfigurationMap.GetSetConfigOption("AffectiveDir", createFullPathFile(configFileReader.getValue("AffectiveDir")));
        affectiveDir.addValidator(new ImageDirectoryValidator("Affective"));

        //See whether the test has practice images and how often they should be repeated.
        TestConfigurationOption<Integer> practiceRepeat = testConfigurationMap.GetSetConfigOption("PracticeRepeat", parseIntWithException("PracticeRepeat", configFileReader.getValue("PracticeRepeat")));
        practiceRepeat.addValidator(new NumberValidator("PracticeRepeat", 0, -1, false));

        //See whether the AAT program should draw colored borders around the images or that the researcher has created it's own cue for the push or pull condition
        boolean doBorders = false;
        if (configFileReader.getValue("ColoredBorders").equalsIgnoreCase("true")) {
            doBorders = true;
        }
        if (configFileReader.getValue("ColoredBorders").equalsIgnoreCase("false")) {
            doBorders = false;
        }


        TestConfigurationOption<Boolean> coloredBorders = testConfigurationMap.GetSetConfigOption("ColoredBorders", doBorders);
        if (coloredBorders.getValue()) {        //Test uses auto-generated colored borders
            TestConfigurationOption<String> pullColor = testConfigurationMap.GetSetConfigOption("BorderColorPull", configFileReader.getValue("BorderColorPull"));
            pullColor.addValidator(new ColorValidator("pull"));
            TestConfigurationOption<String> pushColor = testConfigurationMap.GetSetConfigOption("BorderColorPush", configFileReader.getValue("BorderColorPush"));
            pushColor.addValidator(new ColorValidator("push"));
            testConfigurationMap.GetSetConfigOption("BorderWidth", parseIntWithException("BorderWidth", configFileReader.getValue("BorderWidth")));
        } else {
            //Researcher has specified it's own pull and push cues. Labels to define the push and pull images are necessary now.
            TestConfigurationOption<String> pullTag = testConfigurationMap.GetSetConfigOption("PullTag", configFileReader.getValue("PullTag"));
            pullTag.addValidator(new StringLengthValidator("Pull tag"));
            TestConfigurationOption<String> pushTag = testConfigurationMap.GetSetConfigOption("PushTag", configFileReader.getValue("PushTag"));
            pushTag.addValidator(new StringLengthValidator("Push tag"));

            if (practiceRepeat.getValue() > 0) {   //No border and a practice. Researcher has to supply the test with it's own practice images.
                TestConfigurationOption<File> practiceDir = testConfigurationMap.GetSetConfigOption("PracticeDir", createFullPathFile(configFileReader.getValue("PracticeDir")));
                practiceDir.addValidator(new ImageDirectoryValidator("Practice"));
            }
        }

        if (practiceRepeat.getValue() > 0) {
            if (!testConfigurationMap.contains("practiceDir")) {
                if (coloredBorders.getValue()) {
                    System.out.println("Practice with colored borders");
                    TestConfigurationOption<String> practiceFillColor = testConfigurationMap.GetSetConfigOption("PracticeFillColor", configFileReader.getValue("PracticeFillColor"));
                    practiceFillColor.addValidator(new ColorValidator("Practice fill color"));
                } else {
                    throw new FalseConfigException("When practiceDir isn't set, ColoredBorder has to be set to True \n" +
                            "Or you forgot to set the directory containing the practice images");
                }
            }
        }

        //--------------------- Advanced options  ----------------------------------------------------------------------------------------
        testConfigurationMap.GetSetConfigOption("PlotType", configFileReader.getPlotType(configFileReader.getValue("PlotType")));
        if (!configFileReader.getValue("AffectRatio").equals("")) {
            TestConfigurationOption<String> affectRatio = testConfigurationMap.GetSetConfigOption("AffectRatio", configFileReader.getValue("AffectRatio"));
            affectRatio.addValidator(new RatioValidator("AffectRatio"));
        }

        if (!configFileReader.getValue("NeutralRatio").equals("")) {
            TestConfigurationOption<String> neutralRatio = testConfigurationMap.GetSetConfigOption("NeutralRatio", configFileReader.getValue("NeutralRatio"));
            neutralRatio.addValidator(new RatioValidator("NeutralRatio"));
        }

        if (!configFileReader.getValue("TestRatio").equals("")) {
            TestConfigurationOption<String> testRatio = testConfigurationMap.GetSetConfigOption("TestRatio", configFileReader.getValue("TestRatio"));
            testRatio.addValidator(new RatioValidator("TestRatio"));
        }

        if (!configFileReader.getValue("TrialSize").equals("")) {
            testConfigurationMap.GetSetConfigOption("TrialSize", parseIntWithException("TrialSize", configFileReader.getValue("TrialSize")));
        }

        if (!configFileReader.getValue("MaxSizePerc").equals("")) {
            testConfigurationMap.GetSetConfigOption("MaxSizePerc", parseIntWithException("MaxSizePerc", configFileReader.getValue("MaxSizePerc")));
        }

        if (!configFileReader.getValue("ImageSizePerc").equals("")) {
            testConfigurationMap.GetSetConfigOption("ImageSizePerc", parseIntWithException("ImageSizePerc", configFileReader.getValue("ImageSizePerc")));
        }

        //Set some performance options.
        TestConfigurationOption<Integer> stepSize = testConfigurationMap.GetSetConfigOption("StepSize", parseIntWithException("StepSize", configFileReader.getValue("StepSize")));
        stepSize.addValidator(new NumberValidator("StepSize", 0, 101, true));
        testConfigurationMap.GetSetConfigOption("DataSteps", parseIntWithException("DataSteps", configFileReader.getValue("DataSteps")));
        stepSize.addValidator(new NumberValidator("DataSteps", 0, 101, true));

        return testConfigurationMap;
    }

    private static void addNewIDToConfigFile(File config) {
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
        System.out.println("Test " + file);
        File f = new File(file);
        if (f.isAbsolute()) {
            return f;
        } else {
            return new File(workingDir + File.separator + file);
        }
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

class RatioValidator implements IConfigValidator<String> {

    private String label;

    public RatioValidator(String label) {
        this.label = label;
    }

    @Override
    public void validate(String s) throws FalseConfigException {
        String[] splitted = s.split(":");
        boolean valid = true;
        if (!s.contains(":")) {
            valid = false;
        }
        if (splitted.length > 2) {
            valid = false;
        }
        try {
            Integer.parseInt(splitted[0]);

        } catch (Exception e) {
            valid = false;
        }
        try {
            Integer.parseInt(splitted[1]);

        } catch (Exception e) {
            valid = false;
        }
        if (!valid) {
            throw new FalseConfigException("The ratio for " + label + " is not in a correct format " + s);
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
        int value = integer;
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
        System.out.println("Validating file " + file.getName());
        if (file.getName().length() == 0 || !file.exists() || file.isDirectory()) {
            throw new FalseConfigException(type + " is not configured properly.");
        }
    }
}