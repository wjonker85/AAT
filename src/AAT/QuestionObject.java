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

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 7:50 PM
 * Data structure which contains a optional question from the configuration files. This can be used to show the question + (Answer options)
 * to the screen. The key String is used as a column header for use in a table or CSV file.
 */

public class QuestionObject {

    private String key;
    private String question;
    private ArrayList<String> options;

    public QuestionObject() {
        options = new ArrayList<String>();
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

    public String getKey() {
        return key;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}

