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
import AAT.Configuration.Validation.FalseConfigException;
import DataStructures.AATImage;
import DataStructures.Questionnaire.Questionnaire;
import AAT.Configuration.TestConfiguration;
import IO.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 11/14/11
 * Time: 2:58 PM
 * This class sets up a new test. It starts with checking that all the configuration options are set properly and then
 * assigns the correct values to their corresponding variables. When asked this object can create a random list of images.
 * A practice list and the normal test list.
 */


public abstract class AbstractAAT {

    private TestConfiguration testConfiguration;
    //Different lists, containing the files
    private ArrayList<File> neutralImages = new ArrayList<File>();
    private ArrayList<File> affectiveImages = new ArrayList<File>();
    private ArrayList<File> practiceImages = new ArrayList<File>();
    private Questionnaire questionnaire;
    private HashMap<String, String> translations;
    private int n_pushPerc = 50;
    private int a_pushPerc = 50;
    private int affectPerc = 50;


    /**
     * Constructor Loads all the configuration data from the config file and the language file specified in that config
     * Checks the config file for validity and throws a FalseConfigException when the config contains errors.
     * <p/>
     * //  * @param The config file
     *
     * @throws FalseConfigException When there are mistakes in the config file
     */
    public AbstractAAT(TestConfiguration testConfiguration) throws FalseConfigException {

        this.testConfiguration = testConfiguration;
        System.out.println("TestConfig "+testConfiguration.getPullTag());
    //    System.out.println("bla "+testConfiguration.getNeutralDir().getAbsolutePath());
        neutralImages = XMLReader.getIncludedFilesF(testConfiguration.getNeutralDir());
        if (neutralImages.size() == 0) {
            throw new FalseConfigException("Neutral images directory contains no images");
        }
        //    affectiveImages = getImages(affectiveDir);
        affectiveImages = XMLReader.getIncludedFilesF(testConfiguration.getAffectiveDir());
        if (affectiveImages.size() == 0) {
            throw new FalseConfigException("Affective images directory contains no images");
        }
        if (testConfiguration.getPracticeDir().exists()) {
            practiceImages = XMLReader.getIncludedFilesF(testConfiguration.getPracticeDir());
        }
        a_pushPerc = getPercentage(testConfiguration.getAffectRatio(), "Affect");
        n_pushPerc = getPercentage(testConfiguration.getNeutralRatio(), "Neutral");
        affectPerc = getPercentage(testConfiguration.getTestRatio(), "Test");
        if (testConfiguration.getQuestionnaireFile().exists()) {
            questionnaire = XMLReader.getQuestionnaire(testConfiguration.getQuestionnaireFile());
        }
        translations = XMLReader.getTranslations(testConfiguration.getLanguageFile());
    }

    public TestConfiguration getTestConfiguration() {
        return testConfiguration;
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

    public boolean imageComplete(File neutral, File affective) {
        int aCount = 0;
        int nCount = 0;

        ArrayList<File> aDisk = FileUtils.getImages(affective);
        ArrayList<File> nDisk = FileUtils.getImages(neutral);
        ArrayList<String> aFiles = XMLReader.getIncludedFiles(affective);
        ArrayList<String> nFiles = XMLReader.getIncludedFiles(neutral);
        for (File f : aDisk) {
            System.out.println("Looking for " + f.getName());
            if (aFiles.contains(f.getName())) {
                aCount++;
            }

        }
        for (File f : nDisk) {
            System.out.println("Looking for " + f.getName());
            if (nFiles.contains(f.getName())) {
                nCount++;
            }

            System.out.println("Counted A " + aCount + " disk " + aFiles.size() + "Counted N " + nCount + " disk " + nFiles.size());
        }
        if (nCount == nFiles.size() && aCount == aFiles.size()) {
            return true;
        }

        return false;
    }

    public String getTranslation(String element) {
        return translations.get(element);
    }

    private int getPercentage(String ratio, String s) throws FalseConfigException {
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


    public String getBorderColor(int direction) {
        if (direction == AATImage.PULL) {
            return testConfiguration.getPullColor();
        } else {
            return testConfiguration.getPushColor();
        }
    }

    public String getType(int i) {
        if (i == AATImage.AFFECTIVE) {
            return testConfiguration.getAffectiveDir().getName();
        } else if (i == AATImage.NEUTRAL) {
            return testConfiguration.getNeutralDir().getName();
        } else {
            return "practice";
        }
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public int getN_pushPerc() {
        return n_pushPerc;
    }

    public int getA_pushPerc() {
        return a_pushPerc;
    }

    public int getAffectPerc() {
        return affectPerc;
    }

    public ArrayList<File> getPracticeImages() {
        return practiceImages;
    }

    public ArrayList<File> getAffectiveImages() {
        return affectiveImages;
    }

    public ArrayList<File> getNeutralImages() {
        return neutralImages;
    }

    public int centerPos() {
        return (testConfiguration.getDataSteps() + 1) / 2;
    }

}

