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
import AAT.validation.TestConfigurationMap;
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


    private XMLReader xmlReader;
    private TestConfigurationMap<String> testConfiguration;

    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     *
   //  * @param The config file
     * @throws FalseConfigException When there are mistakes in the config file
     */
    public AatObject (TestConfigurationMap<String> testConfiguration) throws FalseConfigException {

        this.testConfiguration = testConfiguration;
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

    /**
     * Images can have a border created by this program. This method returns that borders color.
     *
     * @param direction push or pull
     * @return The corresponding color
     */
 //   public String getBorderColor(int direction) {
  //      return colorTable.get(direction);
 //   }

    /**
     * Check whether a colored border has to de drawn around an image
     *
     * @return boolean, true for colored borders
     */
//    public boolean hasColoredBorders() {
  //      return coloredBorders;
 //   }

    /**
     * When the test is on a break, a Break text is shown on the screen. This text comes from the languageFile
     *
     * @return Break text to be shown on the screen
     */
 //   public String getBreakText() {
 //       return xmlReader.getValue("break");
 //   }

    /**
     * When the test is started, a general introduction is shown on the screen. This text comes from the languageFile
     *
     * @return The introduction text.
     */
  //  public String getIntroductionText() {
 //       return xmlReader.getValue("introduction");
  //  }

    /**
     * This text is shown after the practice runs. Or when there are no practice runs, this text is the first text to
     * be shown.
     *
     * @return The start text
     */
   // public String getTestStartText() {
   //     return xmlReader.getValue("start");
  //  }

    /**
     * The text to display when the test has ended
     *
     * @return Finished text
     */
  //  public String getTestFinishedText() {
  //      return xmlReader.getValue("finished");
   // }

    /**
     * The stepRate determines in how many steps the the image is resized. The middle number is the middle position
     * of the joystick
     *
     * @return The steprate as specified in the configuration file.
     */
 //   public int getStepRate() {
   //     return stepSize;
  //  }

    /**
     * @return Width of the border around an image
     */
 //   public int getBorderWidth() {
  //      //return borderWidth;
 //   }

    /**
     * @return The file containing the data file
     */
  //  public File getDataFile() {
  //      return dataFile;
  //  }

    /**
     * @return whether the data file contains data
     */

  //  public boolean hasData() {
  //      return dataFile.length() > 0;
  //  }


   // public boolean hasBoxPlot() {
   //     return showBoxPlot;
   // }

  //  public String getPlotType() {
   //     return plotType;
  //  }

    /**
     * @return How many times the test has to be repeated
     */
  //  public int getRepeat() {
     //   return repeat;
   // }

    /**
     * @return When the test needs a break
     */
  //  public int getBreakAfter() {
   //     return breakAfter;
  //  }

  //  public boolean hasPractice() {
  //      return practice;
  //  }

  //  public int getDataSteps() {
   //     return dataSteps;
  //  }

  //  public int centerPos() {
      //  return (dataSteps + 1) / 2;
  //  }

  //  public String getDisplayQuestions() {
   //     return displayQuestions;
  //  }

  //  public String getPullTag() {
  //      return pullTag;
 //   }

 //   public String getPushTag() {
   //     return pushTag;
  //  }

 //   public String getAffectiveDir() {
   //     return aDir;
  //  }

 //   public String getNeutralDir() {
  //      return nDir;
  //  }

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

  //  public int getTest_id() {
 //       return test_id;
 //   }

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

  //  public int getMaxSizePerc() {
   //     return maxSizePerc;
  //  }

 //   public int getImageSizePerc() {
  //      return imageSizePerc;
  //  }

  //  public Questionnaire getQuestionnaire() {
  //      return questionnaire;
  //  }


}

