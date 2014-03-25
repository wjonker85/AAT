package AAT.Configuration;

import java.io.File;

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
    private File neutralDir,affectiveDir, practiceDir;
    private File questionnaireFile;
    private File languageFile;
    private int stepSize, dataSteps, maxSizePerc, imageSizePerc;
    private File dataFile;
    private int testID;
    private String plotType = "BoxPlot";

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

    public File getNeutralDir() {
        return neutralDir;
    }

    public void setNeutralDir(File neutralDir) {
        this.neutralDir = neutralDir;
    }

    public File getAffectiveDir() {
        return affectiveDir;
    }

    public void setAffectiveDir(File affectiveDir) {
        this.affectiveDir = affectiveDir;
    }

    public File getPracticeDir() {
        return practiceDir;
    }

    public void setPracticeDir(File practiceDir) {
        this.practiceDir = practiceDir;
    }

    public File getQuestionnaireFile() {
        return questionnaireFile;
    }

    public void setQuestionnaireFile(File questionnaireFile) {
        this.questionnaireFile = questionnaireFile;
    }

    public File getLanguageFile() {
        return  languageFile;
    }

    public void setLanguageFile(File languageFile) {
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

    public boolean getHasPractice() {
        return hasPractice;
    }

    public void setHasPractice(boolean hasPractice) {
        this.hasPractice = hasPractice;
    }

    public boolean getHasQuestionnaire() {
        return hasQuestionnaire;
    }

    public void setHasQuestionnaire(boolean hasQuestionnaire) {
        this.hasQuestionnaire = hasQuestionnaire;
    }

    public File getDataFile() {
        return dataFile;
    }

    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }

    public String getPlotType() {
        return plotType;
    }
}
