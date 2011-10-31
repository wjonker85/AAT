package DataStructures;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 1:29 PM
 * This class registers all the actions taken by the participant, together with the time it has taken to perform that action
 * since the moment the picture this measurement belongs to was shown on the screen.
 * These measurements can be used to calculate reactionTime. It's also possible to see how the participant moved the joystick.
 * E.g. Directly in the right direction or first to the wrong direction.
 *
 */
public class MeasureData {

    private ArrayList<MeasureObject> allMeasures;  //Every single measure has a different measure object.
    private int id;

    //Constructor creates a empty arrayList of MeasureObjects.
    public MeasureData(int id) {
        this.id = id;
        allMeasures = new ArrayList<MeasureObject>();
    }

    //New measure for each picture
    public void newMeasure(int run, String imageName, int direction, int type) {
        MeasureObject measureObject = new MeasureObject(run, imageName, direction, type);
        allMeasures.add(measureObject);

    }

    //Everytime the participant changes the joystick, it's movement is recorded.
    public void addResult(int size, long time) {
        allMeasures.get(allMeasures.size()-1).addResult(size, time);
    }

    public int getId() {
        return id;
    }


    public ArrayList<Long> getMeasures(int direction, int type) {
        ArrayList<Long> results = new ArrayList<Long>();
        for(MeasureObject measure : allMeasures) {
             if(measure.getDirection() == direction && measure.getType() == type) {
                 results.add(measure.getReactionTime());
             }
        }
        return results;
    }

    //Returns a tablemodel with all results.
    public AbstractTableModel getAllResults() {
        ResultsDataTableModel allResults = new ResultsDataTableModel();

        for (MeasureObject mObject : allMeasures) {
        //    System.out.println(mObject.getImageName()+" "+mObject.size());
            for (int x = 0; x < mObject.size(); x++) {
            ArrayList<Object> imageResults = new ArrayList<Object>();
            imageResults.add(getId());
                imageResults.add(mObject.getRun());
                imageResults.add(mObject.getImageName());
                imageResults.add(mObject.getDirection());
                imageResults.add(mObject.getType());
                imageResults.add(mObject.getPosition(x));
                imageResults.add(mObject.getTime(x));
                allResults.add(imageResults);
            }
        }
        return allResults;
    }
}


/*
A DataStructures.MeasureObject contains the measurements for a single picture. For this picture all the movements from the joystick are recorded
 */
class MeasureObject {

    private ArrayList<Integer> positionList;
    private ArrayList<Long> timeList;

    private int run;
    private String imageName;
    private int direction;
    private int type;

    public MeasureObject(int run, String imageName, int direction, int type) {
        positionList = new ArrayList<Integer>();
        timeList = new ArrayList<Long>();
        this.run = run;
        this.imageName = imageName;
        this.direction = direction;
        this.type = type;

    }

    public void addResult(int position, long time) {
        positionList.add(position);
        timeList.add(time);
    }


    //Returns the current direction (Push or Pull)
    public int getDirection() {
        return direction;
    }


    //Returns if it's an affective or neutral image
    public int getType() {
        return type;
    }

    //Returns the total reactionTime
    public long getReactionTime() {
        return timeList.get(size()-1);  //laatste object returnen
    }

    //Current run.
    public int getRun() {
        return run;
    }

    public String getImageName() {
        return imageName;
    }

    public int size() {
        return positionList.size();
    }

    public int getPosition(int i) {
        return positionList.get(i);
    }

    public long getTime(int i) {
        return timeList.get(i);
    }
}