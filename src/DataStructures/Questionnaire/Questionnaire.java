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

package DataStructures.Questionnaire;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 5/5/12
 * Time: 4:16 PM
 * Simple class containing the Questionnaire data
 */
public class Questionnaire {

    private String introduction = "";
    private ArrayList<AbstractQuestion> allQuestions;

    public Questionnaire(ArrayList<AbstractQuestion> allQuestions, String introduction) {
        this.allQuestions = allQuestions;
        this.introduction = introduction;

    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * In the language file it is possible to add extra questions that are asked to the participant before the
     * real test is started.
     *
     * @return ArrayList with questionObjects. These objects are passed to the questionsView that displays them
     */
    public ArrayList<AbstractQuestion> getExtraQuestions() {
        return allQuestions;
    }
}
