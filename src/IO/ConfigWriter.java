package IO;

import AAT.Configuration.TestConfiguration;
import AAT.Util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by marcel on 3/16/14.
 * This class contains the functionality to write the configuration file. It is called from the configuration builder. The configuration is a simple
 * text file containing all the variables set in the configuration builder. Advanced users could also edit the config file directly. For them comment is
 * added to the file too, so that all the options have a small explanantion.
 */
public class ConfigWriter {

    public static void writeToFile(File file, TestConfiguration configuration) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        String firstheader = "# Configuration file for the AAT.\n" +
                "#\n" +
                "# The next options are for determining the course of the test.\n" +
                "#\n" +
                "# Trials - defines the number of trails\n" +
                "# BreakAfter - after how much Trails the test is on a break. Set this to a higher number than the trails\n" +
                "#\t       if a break is not necessary.\n" +
                "# PracticeRepeat - How many practice images are needed at the beginning of the test. The total number of images\n" +
                "# \t\t   Shown is twice this amount. (Every image has a pull and push variant). Comment this or set to 0 when you don't want a practice.\n" +
                "# DisplayQuestions - Should the questions be asked Before or After the test. Set to None for no questions\n" +
                "# ShowBoxPlot - Set this to True when you want to display a boxplot showing the results, otherwise set to False\n";
        String ratioHeader = "#The next two options are only needed when you want to change the ratio push/pull in the two conditions (push:pull). Default is 50% push and 50% #pull 1:1 ratio.\n";
        String testRatioHeader = "#This option specifies the ratio of Affective vs Neutral images (Affect:Neutral). Default is a 1:1 ratio. \n";
        String NoImagesHeader = "#This option sets the number of images shown in each trial. Normally this options doesnt need to be set. Only needed when you change the ratio('s)\n" +
                "#and want a specified number of images in each trial";
        String colorsHeader = "#The next options are for the way images should be shown on screen. A test can use the build in method that can create a colored border around\n" +
                "#an image.\n" +
                "# ColoredBorders - If this is set to True, the program will show a colored border around the image\n" +
                "# BorderColorPush - Determines the color to be used for the push images. Color is in hex value.\n" +
                "# BorderColorPull - Same for the pull images.\n" +
                "# BorderWidth - Specifies the width the border has in the center position.\n" +
                "# PracticeFillColor - Specifies the color the practice image gets. This only works if the PracticeDir is not set\n";
        String tagsHeader = "# PullTag & PushTag - These are needed when ColoredBorders is set to False. Image file names should contain these tags.";
        String dirHeader = "#Next options are for the specification of directories\n" +
                "# AffectiveDir - This is the directory containing the affective images\n" +
                "# NeutralDir - The directory containing the neutral images\n" +
                "# PracticeDir - A directory containing Practice images. If this is not set, the program will use self-generated images with the specified fill\n" +
                "# color. It will also use the border colors that are specified. \n";
        String languageHeader = "#It is possible for the same test to have different languages. \n" +
                "#LanguageFile specifies the language file used for this test. Change this value to another language file for the test to be performed in a \n" +
                "#different language.";
        String dataFileHeader = "#The next option specifies in which file the data will be saved. When not set it will default to Data.xml";
        String questionHeader = "#When a Questionnaire is added to the AAT, the next option specifies which file contains those questions. ";
        String performanceHeader = "#Next options are for test performance\n" +
                "# StepSize - Determines in how many steps the image is resized. This has to be an odd number. A higher number is smoother, but setting this  \n" +
                "#\t     too high can be bad for performance. (Defaults to 31 when not set)\n" +
                "# DataSteps - Determines the accuracy for data recording. Higher value means smaller movements are recorded, but this also increases\n" +
                "#\t     the error rate. (Defaults to 9 when not set)\n" +
                "# MaxSizePerc - Determines how large the image can be. Value is percentage of the screen height. Can be >100% (Default 100%)\n" +
                "# ImageSizePerc - Determines how large the image will be when first shown on the screen. Value is percentage of the screen height. Can be >100%\n" +
                "#\t\t  (Default 50%)";
        pw.write(firstheader);
        pw.println();
        pw.write("Trials " + configuration.getTrials());
        pw.println();
        pw.write("BreakAfter " + configuration.getBreakAfter());
        pw.println();
        pw.write("PracticeRepeat " + configuration.getPracticeRepeat());
        pw.println();
        pw.write("DisplayQuestions " + configuration.getDisplayQuestions());
        pw.println();
        String boxPlot = "False";
        String plotType = "";
        if (configuration.getShowBoxPlot()) {
            boxPlot = "True";
            plotType = "boxplot";
        }
        pw.write("ShowBoxPlot " + boxPlot);
        pw.println();
        pw.write("PlotType " + plotType);
        pw.println();
        pw.write(ratioHeader);
        pw.println();
        pw.println();
        pw.write("AffectRatio " + checkForValue(configuration.getAffectRatio()));
        pw.println();
        pw.write("NeutralRatio " + checkForValue(configuration.getNeutralRatio()));
        pw.println();
        pw.println();
        pw.write(testRatioHeader);
        pw.println();
        pw.println();
        pw.write("TestRatio " + checkForValue(configuration.getTestRatio()));
        pw.println();
        pw.println();
        pw.write(NoImagesHeader);
        pw.println();
        pw.println();
        if (configuration.getTrialSize() > 0) {
            pw.write("TrialSize " + configuration.getTrialSize());
        } else {
            pw.write("# TrialSize 20");
        }
        pw.println();
        pw.println();
        pw.write(colorsHeader);
        pw.println();
        pw.println();
        String hasBorders = "False";
        if (configuration.getColoredBorders()) {
            hasBorders = "True";
        }
        pw.write("ColoredBorders " + hasBorders);
        pw.println();
        if (configuration.getColoredBorders()) {
            String pullHex = configuration.getPullColor().substring(2, configuration.getPullColor().length());
            // System.out.println(pullHex);
            System.out.println(configuration.getPushColor());
            String pushHex = configuration.getPushColor().substring(2, configuration.getPushColor().length());
            pw.write("BorderColorPush " + pushHex.toUpperCase());
            pw.println();
            pw.write("BorderColorPull " + pullHex.toUpperCase());
            pw.println();
            pw.write("BorderWidth " + configuration.getBorderWidth());
            pw.println();
        } else {
            pw.write("# BorderColorPush F5FE02");
            pw.println();
            pw.write("# BorderColorPull 00A4E7");
            pw.println();
        }
        if (configuration.getPracticeFill()) {
            String fillHex = configuration.getPracticeFillColor();
            fillHex = fillHex.substring(2, fillHex.length());
            pw.write("PracticeFillColor " + fillHex.toUpperCase());
            pw.println();
        } else {
            pw.write("# PracticeFillColor FFDEDE");
            pw.println();
        }
        pw.println();
        pw.write(tagsHeader);
        pw.println();
        pw.println();
        if (configuration.getColoredBorders()) {
            pw.write("# PullTag pull");
            pw.println();
            pw.write("# PushTag push");
            pw.println();
        } else {
            if (configuration.getPullTag().length() > 0) {
                pw.write("PullTag " + configuration.getPullTag());
            } else {
                pw.write("#PullTag");
            }
            pw.println();
            if (configuration.getPushTag().length() > 0) {
                pw.write("PushTag " + configuration.getPushTag());
            } else {
                pw.write("#PushTag");
            }
            pw.println();
        }
        pw.println();
        pw.write(dirHeader);
        pw.println();
        pw.println();
        System.out.println("WORKING " + configuration.getWorkingDir() + " " + configuration.getAffectiveDir());
        if (configuration.getAffectiveDir().exists()) {
            pw.write("AffectiveDir " + FileUtils.getRelativePath(configuration.getWorkingDir(), configuration.getAffectiveDir()));
        }
        pw.println();
        if (configuration.getNeutralDir().exists()) {
            pw.write("NeutralDir " + FileUtils.getRelativePath(configuration.getWorkingDir(), configuration.getNeutralDir()));
        }
        pw.println();
        if (configuration.getPracticeDir().exists()) {
            pw.write("PracticeDir " + FileUtils.getRelativePath(configuration.getWorkingDir(), configuration.getPracticeDir()));
            pw.println();
        } else {
            pw.write("# PracticeDir practice");
            pw.println();
        }
        pw.println();
        pw.write(dataFileHeader);
        pw.println();
        pw.println();
        pw.write("# Data.xml");
        pw.println();
        pw.write(languageHeader);
        pw.println();
        pw.println();
        pw.write("LanguageFile " + FileUtils.getRelativePath(configuration.getWorkingDir(), configuration.getLanguageFile()));
        pw.println();
        pw.println();
        pw.write(questionHeader);
        pw.println();
        pw.println();
        if (configuration.getDisplayQuestions().equalsIgnoreCase("None")) {
            pw.write("# Questionnaire Questionnaire.xml");
            pw.println();
        } else {
            if (configuration.getQuestionnaireFile().getName().length() > 0) {
                pw.write("Questionnaire " + FileUtils.getRelativePath(configuration.getWorkingDir(), configuration.getQuestionnaireFile()));
            } else {
                pw.write("#Questionnaire ");
            }
            pw.println();
        }
        pw.println();
        pw.print(performanceHeader);
        pw.println();
        pw.println();
        pw.println("StepSize " + configuration.getStepSize());
        pw.println("DataSteps " + configuration.getDataSteps());
        pw.println("MaxSizePerc " + configuration.getMaxSizePerc());
        pw.println("ImageSizePerc " + configuration.getImageSizePerc());
        pw.println();
        pw.println();
        pw.println("# Unique ID value. This value is used to determine whether this file has changed since the last time the test was taken.");
        pw.println("ID " + configuration.getTestID());
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

    static private String checkForValue(String input) {
        if (input.equals("")) {
            return "NA";
        } else {
            return input;
        }
    }
}
