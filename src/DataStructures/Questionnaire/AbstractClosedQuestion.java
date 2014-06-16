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
 * Created by marcel on 3/25/14.
 * Abstract super class for an closed question.
 */
public abstract class AbstractClosedQuestion extends AbstractQuestion {

    private ArrayList<String> options;

    public AbstractClosedQuestion() {
        options = new ArrayList<String>();
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
