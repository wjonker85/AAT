package DataStructures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 4/19/12
 * Time: 3:36 PM
 * /*
 * A DataStructures.ImageMeasureData contains the measurements for a single picture. For this picture all the movements from the joystick are recorded
 */

public class ImageMeasureData {


    private ArrayList<Integer> positionList;
    private ArrayList<Long> timeList;

    private int trial;
    private String imageName;
    private int direction;
    private int type;

    public ImageMeasureData(int trial, String imageName, int direction, int type) {
        positionList = new ArrayList<Integer>();
        timeList = new ArrayList<Long>();
        this.trial = trial;
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
        return timeList.get(size() - 1);  //laatste object returnen
    }

    //Current trial.
    public int getTrial() {
        return trial;
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

    public int getFirstPosition() {
        return getPosition(0);
    }
}

