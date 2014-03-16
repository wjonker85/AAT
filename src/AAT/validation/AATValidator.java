package AAT.validation;

import AAT.AatObject;
import DataStructures.AATImage;
import DataStructures.Questionnaire;
import DataStructures.TestConfiguration;
import IO.ConfigFileReader;
import IO.XMLReader;

import java.io.*;
import java.util.Hashtable;
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
     * workingDir
     * testID
     * dataFile
     * langFile
     * DisplayQuestions
     * "questionnaireFile"
     * neutralDir
     * affectiveDir
     * practiceDir
     * coloredBorders
     */
    public static File workingDir;

    public static TestConfiguration ValidateTestConfig(File config) throws FalseConfigException {

        TestConfigurationMap<String> testConfigurationMap = new TestConfigurationMap<String>();
        ConfigFileReader configFileReader = new ConfigFileReader(config);
        workingDir = new File(config.getParentFile().getAbsolutePath());
        testConfigurationMap.GetSetConfigOption("workingDir", workingDir);
        String idValue = "1";
        Boolean newID = true;
        if (configFileReader.getValue("ID").length() > 0) {
            idValue = configFileReader.getValue("ID");
            newID = false;
        }
        TestConfigurationOption id = testConfigurationMap.GetSetConfigOption("testID", configFileReader.getValue("ID"));
        id.addValidator(new IConfigValidator<String>() {
            @Override
            public void validate(String s) throws FalseConfigException {
                try {
                    Integer.parseInt(s);
                } catch (Exception e) {
                    throw new FalseConfigException("Test id value is not a correct integer value.");
                }
            }
        });


        //TODO uitzoeken wat de teststatus is. Indien nodig moet de datafile verandert worden en de oudere gebackuped worden.
        String datFile = configFileReader.getValue("DataFile");

        if (datFile.equals("")) {
            datFile = "Data.xml"; //Set to default.
        }
        testConfigurationMap.GetSetConfigOption("dataFile", datFile);
        TestConfigurationOption<File> lFile = testConfigurationMap.GetSetConfigOption("langFile", createFullPathFile(configFileReader.getValue("LanguageFile")));
        lFile.addValidator(new FileExistsValidator("Language"));
        TestConfigurationOption<String> displayQuestions = testConfigurationMap.GetSetConfigOption("displayQuestions", configFileReader.getValue("DisplayQuestions"));
        displayQuestions.addValidator(new IConfigValidator<String>() {
            @Override
            public void validate(String s) throws FalseConfigException {
                  if(!(s.equalsIgnoreCase("Before") || s.equalsIgnoreCase("Afters") || s.equalsIgnoreCase("None"))) {
                      throw new FalseConfigException("DisplayQuestions should be either Before, After or None");
                  }
            }
        });
        TestConfigurationOption<File> questionnaireFile = testConfigurationMap.GetSetConfigOption("questionnaireFile",createFullPathFile(configFileReader.getValue("Questionnaire")));
        questionnaireFile.addValidator(new FileExistsValidator("Questionnaire"));
        TestConfigurationOption<File> neutralDir = testConfigurationMap.GetSetConfigOption("neutralDir",createFullPathFile(configFileReader.getValue("NeutralDir")));
        neutralDir.addValidator(new ImageDirectoryValidator("Neutral"));
        TestConfigurationOption<File> affectiveDir = testConfigurationMap.GetSetConfigOption("affectiveDir",createFullPathFile(configFileReader.getValue("AffectiveDir")));
        affectiveDir.addValidator(new ImageDirectoryValidator("Affective"));
        TestConfigurationOption<File> practiceDir = testConfigurationMap.GetSetConfigOption("practiceDir",createFullPathFile(configFileReader.getValue("PracticeDir")));
        practiceDir.addValidator(new ImageDirectoryValidator("Practice"));
        boolean doBorders = false;
        if(configFileReader.getValue("ColoredBorders").equalsIgnoreCase("true")) {
            doBorders = true;
        }
        if(configFileReader.getValue("ColoredBorders").equalsIgnoreCase("false")) {
            doBorders = false;
        }
        TestConfigurationOption<Boolean> coloredBorders = testConfigurationMap.GetSetConfigOption("coloredBorders",doBorders);

     //   XMLReader xmlReader = new XMLReader(testConfiguration.getLanguageFile());       TODO andere oplossing hiervoor
        //TODO
        //    xmlReader.addQuestionnaire(questionFile);      TODO andere optie

      //  }
        // id = getHighestID();


//        if (!imageComplete(neutralDir, affectiveDir)) {             //TODO ook naar een andere plek
  //          throw new FalseConfigException("Some of the configured images are not present");
    //    }

        //    xmlReader = new TextReader(languageFile);

        // neutralImages = getImages(neutralDir);

       // neutralImages = xmlReader.getIncludedFilesF(neutralDir);     //TODO dit ook

    //    System.out.println("Neutral " + configFileReader.getValue("NeutralDir") + " " + neutralDir);
    //    if (neutralImages.size() == 0) {
    //        throw new FalseConfigException("Neutral images directory contains no images");
    //    }
        //    affectiveImages = getImages(affectiveDir);
     //   affectiveImages = xmlReader.getIncludedFilesF(affectiveDir);
      //  if (affectiveImages.size() == 0) {
      //      throw new FalseConfigException("Affective images directory contains no images");
      //  }
      //  if (hasQuestions) {
      //      questionnaire = new Questionnaire(xmlReader.getExtraQuestions(), xmlReader.getQuestionnaireIntro());
      //  }

        if (coloredBorders.getValue()) {

            System.out.println("Colored borders is set to True");
            TestConfigurationOption<File> pullColor = testConfigurationMap.GetSetConfigOption("borderColorPull",createFullPathFile(configFileReader.getValue("BorderColorPull")));
            pullColor.addValidator(new ColorValidator("pull"));
            TestConfigurationOption<File> pushColor = testConfigurationMap.GetSetConfigOption("borderColorPush",createFullPathFile(configFileReader.getValue("BorderColorPush")));
            pushColor.addValidator(new ColorValidator("push"));
            try {
                testConfigurationMap.GetSetConfigOption("borderWidth",Integer.parseInt(configFileReader.getValue("BorderWidth")));
            } catch (Exception e) {
                throw new FalseConfigException("Border width is not configured properly");
            }
        } else {       //Check for pull and push tag
            System.out.println("Colored borders is set to False:");
            testConfigurationMap.GetSetConfigOption("pullTag",configFileReader.getValue("PullTag"));
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
                    System.out.println("Practice dir: " + practDir);
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
        System.out.println("Number of Trials is" + configFileReader.getValue("Trials"));
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
                    practiceDir = new File(workingDir + File.separator + practDir);
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
                throw new AatObject.FalseConfigException("TrialSize is not set to a correct number");
            }
        }

        if (!configFileReader.getValue("MaxSizePerc").equals("")) {
            try {
                maxSizePerc = Integer.parseInt(configFileReader.getValue("MaxSizePerc"));
            } catch (Exception e) {
                throw new AatObject.FalseConfigException("Maximum image size is not a number");
            }
            if (maxSizePerc <= 0) {
                throw new AatObject.FalseConfigException("Maximum image size should be larger than 0");
            }
        }
        if (!configFileReader.getValue("ImageSizePerc").equals("")) {
            try {
                imageSizePerc = Integer.parseInt(configFileReader.getValue("ImageSizePerc"));
            } catch (Exception e) {
                throw new AatObject.FalseConfigException("Image start size percentage is not a number");
            }
            if (imageSizePerc <= 0) {
                throw new AatObject.FalseConfigException("Image start size percentage should be larger than 0");
            }
        }
        if (breakAfter == repeat) {
            throw new FalseConfigException("Number of trials and Break After values cannot be the same");
        }


        if (newID) {          //Add a new id value to the config file.    //TODO dit ergens achteraan plaatsen.
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
    }


    public static File createFullPathFile(String file) {
        return new File(workingDir + File.separator + file);
    }

    //Check whether a given directory is a valid directory.
    class ImageDirectoryValidator implements IConfigValidator<File>
    {
       private String type;

        public ImageDirectoryValidator(String type)
        {
            this.type = type;
        }

        @Override
        public void validate(File dir) throws FalseConfigException {
            if (dir.getName().equals("") || !dir.isDirectory()) {
                throw new FalseConfigException("Directory for the "+type+" images is not set properly");
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
           if(s.length() == 0) {
               throw new FalseConfigException(label+" is not set to a correct value");
           }
       }
   }
}

    class ColorValidator implements IConfigValidator<String> {

        private static final String HEX_PATTERN = "(^[0-9A-F]+$)";
        private String label;
        Pattern hexPattern;

        public ColorValidator(String label)
        {
            hexPattern = Pattern.compile(HEX_PATTERN);
            this.label = label;
        }

        @Override
        public void validate(String s) throws FalseConfigException {
            Matcher matcher = hexPattern.matcher(s);
            if (!(s.length() == 6) || !matcher.matches()) {
                throw new FalseConfigException("The color specified for the "+label+" border is not a valid 6 character hex value");
            }
        }
    }



    //Check whether a specified file exists
    class FileExistsValidator implements IConfigValidator<File>  {

       private String type;
        public FileExistsValidator(String type) {
              this.type = type;
        }

        @Override
        public void validate(File file) throws FalseConfigException {
            if(file.getName().length() == 0 || !file.exists() || file.isDirectory())  {
                   throw new FalseConfigException(type +" is not configured properly.")
            }
        }
    }






