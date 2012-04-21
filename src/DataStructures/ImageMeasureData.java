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

