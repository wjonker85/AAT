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
package DataStructures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 1:29 PM
 * This class registers all the actions taken by the participant, together with the time it has taken to perform that action
 * since the moment the picture this measurement belongs to was shown on the screen.
 * These measurements can be used to calculate reactionTime. It's also possible to see how the participant moved the joystick.
 * E.g. Directly in the right direction or first to the wrong direction.
 */
public class ParticipantData {

    //  private ArrayList<ImageMeasureData> allImageMeasures;  //Every single measure has a different measure object.
    private HashMap<String, String> questionData = null;
    private HashMap<Integer, ArrayList<ImageMeasureData>> allMeasures;   //Records a list per trial
    private int id, test_id;
    ArrayList<ImageMeasureData> currentTrialMeasure;

    //Constructor creates a empty arrayList of MeasureObjects.
    public ParticipantData(int id, int test_id) {
        this.id = id;
        this.test_id = test_id;
        //      allImageMeasures = new ArrayList<ImageMeasureData>();
        allMeasures = new HashMap<Integer, ArrayList<ImageMeasureData>>();
    }


    public void addQuestionData(HashMap<String, String> questionData) {    //Add extra participant info, based on the Questionnaire
        this.questionData = questionData;
    }

    //New measure for each picture per trial.
    public void newMeasure(int trial, String imageName, int direction, int type) {
        ImageMeasureData imageMeasureData = new ImageMeasureData(trial, imageName, direction, type);
        if (!allMeasures.containsKey(trial)) {  //Add new trial
            currentTrialMeasure = new ArrayList<ImageMeasureData>();
            allMeasures.put(trial, currentTrialMeasure);
        } else {
            currentTrialMeasure = allMeasures.get(trial);
        }
        currentTrialMeasure.add(imageMeasureData);
        //    allImageMeasures.add(imageMeasureData);

    }

    //Everytime the participant changes the joystick, it's movement is recorded.
    public void addResult(int size, long time) {
        //   allImageMeasures.get(allImageMeasures.size() - 1).addResult(size, time);
        currentTrialMeasure.get(currentTrialMeasure.size() - 1).addResult(size, time);
    }

    public int getId() {
        return id;
    }

    public int getTestID() {
        return test_id;
    }

    /**
     * Creates an array with the values for a given direction and type. For all trials combined. Needed to show
     * the boxplot at the end of the test
     *
     * @param direction push or pull
     * @param type      affect or neutral
     * @return array float[] containing the results
     */
    public float[] getMeasures(int direction, int type) {
        ArrayList<Long> results = new ArrayList<Long>();
        for (int key : allMeasures.keySet()) {
            for (ImageMeasureData imageMeasure : allMeasures.get(key)) {
                if (imageMeasure.getDirection() == direction && imageMeasure.getType() == type) {
                    results.add(imageMeasure.getReactionTime());
                }
            }
        }
        return convertToArray(results);
    }

    public ArrayList<ImageMeasureData> getMeasurements(int trial) {    //return the list for a trial
        return allMeasures.get(trial);
    }

    public HashMap<String, String> getQuestionnaire() {
        return questionData;
    }

    /**
     * Converts The measurement data to an array. The boxplots needs float arrays as input.
     *
     * @param input ArrayList containing longs.
     * @return Array containing floats
     */
    private float[] convertToArray(ArrayList<Long> input) {
        float[] array = new float[input.size()]; //Create a new array
        for (int x = 0; x < input.size(); x++) {
            long l = input.get(x);
            array[x] = (float) l;
        }
        return array;
    }
}


