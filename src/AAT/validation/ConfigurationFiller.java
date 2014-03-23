package AAT.validation;

import DataStructures.TestConfiguration;

/**
 * Created by marcel on 3/23/14.
 */
public class ConfigurationFiller {


    //All the configuration options
    public static String[] options = {
            "ColoredBorders",
            "BorderColorPush",
            "BorderColorPull",
            "BorderWidth",
            "StepSize",
            "DataSteps",
            "Trials",
            "BreakAfter",
            "AffectiveDir",
            "NeutralDir",
            "LanguageFile",
            "PracticeDir",
            "PracticeFillColor",
            "PracticeRepeat",
            "NoPracticeTrials",
            "ParticipantsFile",
            "DataFile",
            "PullTag",
            "PushTag",
            "DisplayQuestions",
            "ShowBoxPlot",
            "Questionnaire",
            "AffectRatio",
            "NeutralRatio",
            "TestRatio",
            "TrialSize",
            "MaxSizePerc",
            "ImageSizePerc",
            "ID",
            "PlotType"

    };


    public static TestConfiguration fillTestConfiguration(TestConfigurationMap<String> validatedConfiguration) {
        TestConfiguration testConfiguration = new TestConfiguration();
        try {
            testConfiguration.setTrials(validatedConfiguration.getIntValue("Trials"));
            testConfiguration.setBreakAfter(validatedConfiguration.getIntValue("BreakAfter"));
            testConfiguration.setPracticeRepeat(validatedConfiguration.getIntValue("PracticeRepeat"));
            testConfiguration.setDisplayQuestions(validatedConfiguration.getStringValue("DisplayQuestions"));
            testConfiguration.setAffectRatio(validatedConfiguration.getStringValue("AffectRatio"));
            testConfiguration.setNeutralRatio(validatedConfiguration.getStringValue("NeutralRatio"));
            testConfiguration.setTestRatio(validatedConfiguration.getStringValue("TestRatio"));
            testConfiguration.setTrialSize(validatedConfiguration.getIntValue("TrialSize"));
            testConfiguration.setColoredBorders(validatedConfiguration.getBooleanValue("ColoredBorders"));
            testConfiguration.setShowBoxPlot(validatedConfiguration.getBooleanValue("ShowBoxPlot"));
            testConfiguration.setPullColor(validatedConfiguration.getStringValue("BorderColorPull"));
            testConfiguration.setPushColor(validatedConfiguration.getStringValue("BorderColorPush"));
            testConfiguration.setBorderWidth(validatedConfiguration.getIntValue("BorderWidth"));
            //  testConfiguration.setPracticeFill(validatedConfiguration.getBooleanValue("ColoredBorders"));   TODO naar kijken of dit wordt gebruikt
            testConfiguration.setPracticeFillColor(validatedConfiguration.getStringValue("PracticeFillColor"));
            testConfiguration.setPullTag(validatedConfiguration.getStringValue("PullTag"));
            testConfiguration.setPushTag(validatedConfiguration.getStringValue("PushTag"));
            testConfiguration.setNeutralDir(validatedConfiguration.getStringValue("NeutralDir"));
            testConfiguration.setAffectiveDir(validatedConfiguration.getStringValue("AffectiveDir"));
            testConfiguration.setPracticeDir(validatedConfiguration.getStringValue("PracticeDir"));
            testConfiguration.setQuestionnaireFile(validatedConfiguration.getFileValue("Questionnaire"));
            testConfiguration.setLanguageFile(validatedConfiguration.getFileValue("LanguageFile"));
            testConfiguration.setStepSize(validatedConfiguration.getIntValue("StepSize"));
            testConfiguration.setDataSteps(validatedConfiguration.getIntValue("DataSteps"));
            testConfiguration.setMaxSizePerc(validatedConfiguration.getIntValue("MaxSizePerc"));
            testConfiguration.setImageSizePerc(validatedConfiguration.getIntValue("ImageSizePerc"));
            testConfiguration.setTestID(validatedConfiguration.getIntValue("ID"));
            //    testConfiguration.setHasPractice( boolean hasPractice);
            //    testConfiguration.setHasQuestionnaire( boolean hasQuestionnaire);
            testConfiguration.setDataFile(validatedConfiguration.getFileValue("DataFile"));
            testConfiguration.setWorkingDir(validatedConfiguration.getFileValue("WorkingDir"));
        } catch (FalseConfigException e) {

        }
        return testConfiguration;
    }
}

