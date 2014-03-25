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

import AAT.Configuration.Validation.FalseConfigException;
import DataStructures.AATImage;
import AAT.Configuration.TestConfiguration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 11/14/11
 * Time: 3:15 PM
 * Version of the AAT to be run on computers with enough RAM. In this version all the images are preloaded in memory
 * so the test can work faster and record reactionTimes more precise.
 */
public class HighMemoryAAT extends AbstractAAT {


    /**
     * @param testConfiguration The configuration file for the test
     *
     */
    public HighMemoryAAT(TestConfiguration testConfiguration) throws FalseConfigException {
        super(testConfiguration);
    }


    /**
     * Creates a random list with practice images. If there is a directory specified in the config, it will load the
     * imageFile from that directory. Otherwise it will generate a list with self-created images containing a single color
     * as specified in the config.
     *
     * @return arraylist containing practice AATImage objects
     */
    public ArrayList<AATImage> createRandomPracticeList() {
        ArrayList<AATImage> list = new ArrayList<AATImage>();
        int size = getTestConfiguration().getPracticeRepeat() * 2;
        if (getTestConfiguration().getPracticeDir().exists()) {  //image dir is set
            if (getTestConfiguration().getColoredBorders()) {

                for (int x = 0; x < size; x++) {
                      for(File image : getPracticeImages()) {
                  //  for (File image : FileUtils.getImages(practiceDir)) {
                        AATImage pull = new AATImage(image, AATImage.PULL, this, x); //Add push and pull version
                        AATImage push = new AATImage(image, AATImage.PUSH, this, x);
                        list.add(pull);
                        list.add(push);
                    }
                }
            } else {  //Do practice with push and pull tags in image file name
                //TODO: PracticeRepeat

                for (int x = 0; x < getTestConfiguration().getPracticeRepeat(); x++) {
                    for(File image : getPracticeImages()) {
                 //   for (File image : FileUtils.getImages(practiceDir)) {
                        if (image.getName().contains(getTestConfiguration().getPullTag())) {
                            AATImage pull = new AATImage(image, AATImage.PULL, this, x); //Two instances for every image
                            list.add(pull);
                        }
                        if (image.getName().contains(getTestConfiguration().getPushTag())) {
                            AATImage push = new AATImage(image, AATImage.PUSH, this, x);
                            list.add(push);      //Load the neutral images
                        }
                    }
                }
            }
        } else {             //Else let the test create the images itself
            Color c;
            if (getTestConfiguration().getPracticeFillColor().length() > 0) {
                c = Color.decode("#" + getTestConfiguration().getPracticeFillColor());
            } else {
                c = Color.gray;
            }

            int i = 0;
            for (int x = 0; x < size; x++) {
                AATImage pull = new AATImage(AATImage.PULL, c, x, this);
                list.add(pull);
                i++;
                AATImage push = new AATImage(AATImage.PUSH, c, x, this);
                list.add(push);
                i++;
            }
        }
        Collections.shuffle(list); //randomise the list.
        return list;
    }

    /**
     * Creates a randomised list, containing the affective and neutral images. Every image gets loaded twice, one for the
     * pull condition and one for the push condition.
     *
     * @return Randomised arrayList with AATImages
     */
    public ArrayList<AATImage> createRandomListBorders() {
        ArrayList<AATImage> randomList = new ArrayList<AATImage>();
        int trialSize =   getTestConfiguration().getTrialSize();
        if (trialSize <= 0) {  //not set in the config
            trialSize = (getNeutralImages().size() + getAffectiveImages().size()) * 2;
        }
        System.out.println("Trial Size = " + trialSize);
        float aSize = (getAffectPerc() * trialSize) / 100f;
        int affectSize = Math.round(aSize);
        System.out.println("Affect size " + affectSize);
        int neutralSize = trialSize - affectSize;
        randomList.addAll(createList(neutralSize, getN_pushPerc(), getNeutralImages(), AATImage.NEUTRAL));
        randomList.addAll(createList(affectSize, getA_pushPerc(), getAffectiveImages(), AATImage.AFFECTIVE));
        Collections.shuffle(randomList);    //Randomise the list
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Free memory : " + runtime.freeMemory());
        return randomList;
    }

    /**
     * Create a list with different push/pull percentages
     *
     * @param n
     * @param pushPerc
     * @param fileList
     * @return
     */
    public ArrayList<AATImage> createList(int n, int pushPerc, ArrayList<File> fileList, int type) {
        Collections.shuffle(fileList);
        ArrayList<AATImage> returnList = new ArrayList<AATImage>();
        int nPush = (pushPerc * n) / 100;
        for (int x = 0; x < n; x++) {
            File image = fileList.get(x % fileList.size());
            if (x < nPush) {
                AATImage push = new AATImage(image, AATImage.PUSH, type, this);
                returnList.add(push);
            } else {
                AATImage pull = new AATImage(image, AATImage.PULL, type, this); //Two instances for every image
                returnList.add(pull);
            }
        }
        return returnList;
    }

    public ArrayList<AATImage> createRandomListNoBorders() {
        ArrayList<AATImage> randomList = new ArrayList<AATImage>();
        for (File image : getNeutralImages()) {
            // System.out.println(image.getName());
            if (image.getName().contains(getTestConfiguration().getPullTag())) {
                AATImage pull = new AATImage(image, AATImage.PULL, AATImage.NEUTRAL, this); //Two instances for every image
                randomList.add(pull);
            }
            if (image.getName().contains(getTestConfiguration().getPushTag())) {
                AATImage push = new AATImage(image, AATImage.PUSH, AATImage.NEUTRAL, this);
                randomList.add(push);      //Load the neutral images
            }
        }
        for (File image : getNeutralImages()) {
            if (image.getName().contains(getTestConfiguration().getPullTag())) {
                AATImage pull = new AATImage(image, AATImage.PULL, AATImage.AFFECTIVE, this); //Two instances for every image
                randomList.add(pull);
            }
            if (image.getName().contains(getTestConfiguration().getPushTag())) {
                AATImage push = new AATImage(image, AATImage.PUSH, AATImage.AFFECTIVE, this);
                randomList.add(push);      //Load the neutral images
            }
        }
        Collections.shuffle(randomList);    //Randomise the list
        return randomList;
    }
}

