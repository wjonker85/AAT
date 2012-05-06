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

import DataStructures.AATImage;

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
public class HighMemoryAAT extends AatObject {


    /**
     * @param config The configuration file for the test
     * @throws FalseConfigException, when there are configuration errors
     */
    public HighMemoryAAT(File config) throws FalseConfigException {
        super(config);
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
        int size = practiceRepeat * 2;
        if (practiceDir != null) {  //image dir is set
            if (this.hasColoredBorders()) {

                for (int x = 0; x < size; x++) {
                    for (File image : getImages(practiceDir)) {
                        AATImage pull = new AATImage(image, AATImage.PULL, this, x); //Add push and pull version
                        AATImage push = new AATImage(image, AATImage.PUSH, this, x);
                        list.add(pull);
                        list.add(push);
                    }
                }
            } else {  //Do practice with push and pull tags in image file name
                //TODO: PracticeRepeat

                for (int x = 0; x < practiceRepeat; x++) {
                    for (File image : getImages(practiceDir)) {
                        if (image.getName().contains(testConfig.getValue("PullTag"))) {
                            AATImage pull = new AATImage(image, AATImage.PULL, this, x); //Two instances for every image
                            list.add(pull);
                        }
                        if (image.getName().contains(testConfig.getValue("PushTag"))) {
                            AATImage push = new AATImage(image, AATImage.PUSH, this, x);
                            list.add(push);      //Load the neutral images
                        }
                    }
                }
            }
        } else {             //Else let the test create the images itself
            Color c;
            if (practiceFillColor.length() > 0) {
                c = Color.decode("#" + practiceFillColor);
            } else {
                c = Color.gray;
            }

            int i = 0;
            for (int x = 0; x < size; x++) {
                AATImage pull = new AATImage(AATImage.PULL, c, i, this);
                list.add(pull);
                i++;
                AATImage push = new AATImage(AATImage.PUSH, c, i, this);
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
        int affectSize = (affectPerc * trialSize) / 100;
        int neutralSize = trialSize - affectSize;

        randomList.addAll(createList(neutralSize, n_pushPerc, neutralImages, AATImage.NEUTRAL));
        randomList.addAll(createList(affectSize, a_pushPerc, affectiveImages, AATImage.AFFECTIVE));
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
        if (n == 0) {
            n = fileList.size() * 2;
        }
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
        for (File image : neutralImages) {
            // System.out.println(image.getName());
            if (image.getName().contains(testConfig.getValue("PullTag"))) {
                AATImage pull = new AATImage(image, AATImage.PULL, AATImage.NEUTRAL, this); //Two instances for every image
                randomList.add(pull);
            }
            if (image.getName().contains(testConfig.getValue("PushTag"))) {
                AATImage push = new AATImage(image, AATImage.PUSH, AATImage.NEUTRAL, this);
                randomList.add(push);      //Load the neutral images
            }
        }
        for (File image : affectiveImages) {
            if (image.getName().contains(testConfig.getValue("PullTag"))) {
                AATImage pull = new AATImage(image, AATImage.PULL, AATImage.AFFECTIVE, this); //Two instances for every image
                randomList.add(pull);
            }
            if (image.getName().contains(testConfig.getValue("PushTag"))) {
                AATImage push = new AATImage(image, AATImage.PUSH, AATImage.AFFECTIVE, this);
                randomList.add(push);      //Load the neutral images
            }
        }
        Collections.shuffle(randomList);    //Randomise the list
        return randomList;
    }
}

