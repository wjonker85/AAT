package DataStructures;

import AAT.IConfigOption;
import AAT.validation.FalseConfigException;
import AAT.validation.IConfigValidator;

import java.io.File;
import java.util.HashMap;

/**
 * Created by marcel on 3/16/14.
 * Datastructure containing all the user specified variables for an AAT.
 */
public class TestConfiguration {
    private int trials, breakAfter, practiceRepeat;
    private String displayQuestions = "";
    private File workingDir;
    private boolean hasQuestionnaire = false;
    private String AffectRatio = "";
    private String NeutralRatio = "";
    private String TestRatio = "";
    private int trialSize;
    private boolean hasPractice = false;
    private Boolean coloredBorders = false;
    private Boolean showBoxPlot = false;
    private String pullColor = "", pushColor = "";
    private int borderWidth;
    private Boolean practiceFill = false;
    private String practiceFillColor = "";
    private String pullTag = "", pushTag = "";
    private String neutralDir = "", affectiveDir = "", practiceDir = "";
    private String questionnaireFile = "";
    private String languageFile = "";
    private int stepSize, dataSteps, maxSizePerc, imageSizePerc;
    private String dataFile;
    private int testID;

    public int getTrials() {
        return trials;
    }

    public void setTrials(int trials) {
        this.trials = trials;
    }

    public int getBreakAfter() {
        return breakAfter;
    }

    public void setBreakAfter(int breakAfter) {
        this.breakAfter = breakAfter;
    }

    public int getPracticeRepeat() {
        return practiceRepeat;
    }

    public void setPracticeRepeat(int practiceRepeat) {
        this.practiceRepeat = practiceRepeat;
    }

    public String getDisplayQuestions() {
        return displayQuestions;
    }

    public void setDisplayQuestions(String displayQuestions) {
        this.displayQuestions = displayQuestions;
    }

    public String getAffectRatio() {
        return AffectRatio;
    }

    public void setAffectRatio(String affectRatio) {
        AffectRatio = affectRatio;
    }

    public String getNeutralRatio() {
        return NeutralRatio;
    }

    public void setNeutralRatio(String neutralRatio) {
        NeutralRatio = neutralRatio;
    }

    public String getTestRatio() {
        return TestRatio;
    }

    public void setTestRatio(String testRatio) {
        TestRatio = testRatio;
    }

    public int getTrialSize() {
        return trialSize;
    }

    public void setTrialSize(int trialSize) {
        this.trialSize = trialSize;
    }

    public Boolean getColoredBorders() {
        return coloredBorders;
    }

    public void setColoredBorders(Boolean coloredBorders) {
        this.coloredBorders = coloredBorders;
    }

    public Boolean getShowBoxPlot() {
        return showBoxPlot;
    }

    public void setShowBoxPlot(Boolean showBoxPlot) {
        this.showBoxPlot = showBoxPlot;
    }

    public String getPullColor() {
        return pullColor;
    }

    public void setPullColor(String pullColor) {
        this.pullColor = pullColor;
    }

    public String getPushColor() {
        return pushColor;
    }

    public void setPushColor(String pushColor) {
        this.pushColor = pushColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Boolean getPracticeFill() {
        return practiceFill;
    }

    public void setPracticeFill(Boolean practiceFill) {
        this.practiceFill = practiceFill;
    }

    public String getPracticeFillColor() {
        return practiceFillColor;
    }

    public void setPracticeFillColor(String practiceFillColor) {
        this.practiceFillColor = practiceFillColor;
    }

    public String getPullTag() {
        return pullTag;
    }

    public void setPullTag(String pullTag) {
        this.pullTag = pullTag;
    }

    public String getPushTag() {
        return pushTag;
    }

    public void setPushTag(String pushTag) {
        this.pushTag = pushTag;
    }

    public String getNeutralDir() {
        return neutralDir;
    }

    public void setNeutralDir(String neutralDir) {
        this.neutralDir = neutralDir;
    }

    public String getAffectiveDir() {
        return affectiveDir;
    }

    public void setAffectiveDir(String affectiveDir) {
        this.affectiveDir = affectiveDir;
    }

    public String getPracticeDir() {
        return practiceDir;
    }

    public void setPracticeDir(String practiceDir) {
        this.practiceDir = practiceDir;
    }

    public String getQuestionnaireFile() {
        return questionnaireFile;
    }

    public void setQuestionnaireFile(String questionnaireFile) {
        this.questionnaireFile = questionnaireFile;
    }

    public File getLanguageFile() {
        return new File(workingDir + File.separator + languageFile);
    }

    public void setLanguageFile(String languageFile) {
        this.languageFile = languageFile;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getDataSteps() {
        return dataSteps;
    }

    public void setDataSteps(int dataSteps) {
        this.dataSteps = dataSteps;
    }

    public int getMaxSizePerc() {
        return maxSizePerc;
    }

    public void setMaxSizePerc(int maxSizePerc) {
        this.maxSizePerc = maxSizePerc;
    }

    public int getImageSizePerc() {
        return imageSizePerc;
    }

    public void setImageSizePerc(int imageSizePerc) {
        this.imageSizePerc = imageSizePerc;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    public boolean isHasPractice() {
        return hasPractice;
    }

    public void setHasPractice(boolean hasPractice) {
        this.hasPractice = hasPractice;
    }

    public boolean isHasQuestionnaire() {
        return hasQuestionnaire;
    }

    public void setHasQuestionnaire(boolean hasQuestionnaire) {
        this.hasQuestionnaire = hasQuestionnaire;
    }

    public File getDataFile() {
        return new File(workingDir + File.separator + dataFile);
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }
}

class