package AAT.Configuration.Validation;

import AAT.Configuration.TestConfiguration;

/**
 * Fill the testConfig with the validated value which were read from the AAT config file.
 */
public class ConfigurationFiller {

    public static TestConfiguration fillTestConfiguration(TestConfigurationMap<String> validatedConfiguration) {
        TestConfiguration testConfiguration = new TestConfiguration();
        try {
            System.out.println("HIERRRRR");
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
            testConfiguration.setNeutralDir(validatedConfiguration.getFileValue("NeutralDir"));
            testConfiguration.setAffectiveDir(validatedConfiguration.getFileValue("AffectiveDir"));
            testConfiguration.setPracticeDir(validatedConfiguration.getFileValue("PracticeDir"));
            testConfiguration.setQuestionnaireFile(validatedConfiguration.getFileValue("Questionnaire"));
            testConfiguration.setLanguageFile(validatedConfiguration.getFileValue("LanguageFile"));
            testConfiguration.setStepSize(validatedConfiguration.getIntValue("StepSize"));
            testConfiguration.setDataSteps(validatedConfiguration.getIntValue("DataSteps"));
            testConfiguration.setMaxSizePerc(validatedConfiguration.getIntValue("MaxSizePerc"));
            testConfiguration.setImageSizePerc(validatedConfiguration.getIntValue("ImageSizePerc"));
            testConfiguration.setTestID(validatedConfiguration.getIntValue("ID"));
            if(testConfiguration.getPracticeDir().length() >0 || testConfiguration.getPracticeFillColor().length() > 0) {
                testConfiguration.setHasPractice(true);
            }
            else {
                testConfiguration.setHasPractice(false);
            }
            //    testConfiguration.setHasPractice( boolean hasPractice);
            //    testConfiguration.setHasQuestionnaire( boolean hasQuestionnaire);
            testConfiguration.setDataFile(validatedConfiguration.getFileValue("DataFile"));
            testConfiguration.setWorkingDir(validatedConfiguration.getFileValue("WorkingDir"));
        } catch (FalseConfigException e) {
              e.printStackTrace();
        }
        return testConfiguration;
    }
}

