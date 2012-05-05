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
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 7:50 PM
 * Data structure which contains a optional question from the configuration files. This can be used to show the question + (Answer options)
 * to the screen. The key String is used as a column header for use in a table or CSV file.
 */

public class QuestionData {

    private String key;
    private String question;
    private String type;
    private int size;
    private String left;
    private String right;
    private boolean required = true; //Questions are standard required

    private ArrayList<String> options;

    public QuestionData(String type) {
        options = new ArrayList<String>();
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public String getQuestion() {
        return question;
    }

    public void setLeftText(String left) {
        this.left = left;
    }

    public void setRightText(String right) {
        this.right = right;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public String getLeftText() {
        return left;
    }

    public String getRightText() {
        return right;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
