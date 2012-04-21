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

import javax.swing.table.AbstractTableModel;
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

    private ArrayList<ImageMeasureData> allImageMeasures;  //Every single measure has a different measure object.
    private HashMap<String, String> questionData = null;
    private HashMap<Integer, ArrayList<ImageMeasureData>> allMeasures;   //Records a list per trial
    private int id;
    ArrayList<ImageMeasureData> currentTrialMeasure;

    //Constructor creates a empty arrayList of MeasureObjects.
    public ParticipantData(int id) {
        this.id = id;
        allImageMeasures = new ArrayList<ImageMeasureData>();
        allMeasures = new HashMap<Integer, ArrayList<ImageMeasureData>>();
    }

    public ParticipantData(int id, HashMap<String, String> questionData) {
        this.id = id;
        this.questionData = questionData;
    }

    public void addQuestionData(HashMap<String, String> questionData) {    //Add extra participant info, based on the questionnaire
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
        allImageMeasures.add(imageMeasureData);

    }

    //Everytime the participant changes the joystick, it's movement is recorded.
    public void addResult(int size, long time) {
        allImageMeasures.get(allImageMeasures.size() - 1).addResult(size, time);
        currentTrialMeasure.get(currentTrialMeasure.size() - 1).addResult(size, time);
    }

    public int getId() {
        return id;
    }


    public ArrayList<Long> getMeasures(int direction, int type) {
        ArrayList<Long> results = new ArrayList<Long>();
        for (ImageMeasureData imageMeasure : allImageMeasures) {
            if (imageMeasure.getDirection() == direction && imageMeasure.getType() == type) {
                results.add(imageMeasure.getReactionTime());
            }
        }
        return results;
    }

    //Returns a tablemodel with all results.
    public AbstractTableModel getAllResults() {
        ResultsDataTableModel allResults = new ResultsDataTableModel();

        for (ImageMeasureData mDataImage : allImageMeasures) {
            //    System.out.println(mDataImage.getImageName()+" "+mDataImage.size());
            for (int x = 0; x < mDataImage.size(); x++) {
                ArrayList<Object> imageResults = new ArrayList<Object>();
                imageResults.add(getId());
                imageResults.add(mDataImage.getTrial());
                imageResults.add(mDataImage.getImageName());
                imageResults.add(mDataImage.getDirection());
                imageResults.add(mDataImage.getType());
                imageResults.add(mDataImage.getPosition(x));
                imageResults.add(mDataImage.getTime(x));
                allResults.add(imageResults);
            }
        }
        return allResults;
    }

    public ArrayList<ImageMeasureData> getMeasurements() {
        return allImageMeasures;
    }

    public ArrayList<ImageMeasureData> getMeasurements(int trial) {    //return the list for a trial
        return allMeasures.get(trial);
    }

    public HashMap<String, String> getQuestionnaire() {
        return questionData;
    }
}


