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

package Views.Questionnaire;

import DataStructures.Questionnaire.*;

/**
 * Created by marcel on 4/4/14.
 * Visitor which gives the translation used in the selection combo box.
 */
public class QuestionTypeVisitor implements IQuestionVisitor<String> {
    @Override
    public String Visit(ClosedButtonQuestion question) {
        return "Closed question (buttons)";
    }

    @Override
    public String Visit(ClosedComboQuestion question) {
        return "Closed question (combo-box)";
    }

    @Override
    public String Visit(LikertQuestion question) {
        return "Likert Scale";
    }

    @Override
    public String Visit(SemDiffQuestion question) {
        return "Semantic differential";
    }

    @Override
    public String Visit(OpenQuestion question) {
        return "Open question";
    }

    @Override
    public String Visit(OpenTextAreaQuestion question) {
        return "Open question with text area";
    }
}
