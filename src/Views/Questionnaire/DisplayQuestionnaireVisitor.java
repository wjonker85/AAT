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
import Views.Questionnaire.QuestionPanels.*;

/**
 * Created by marcel on 3/25/14.
 * Another implementation of the question visitor. This one returns the correct display panel for each type of question
 * that can be present in the questionnaire
 */
public class DisplayQuestionnaireVisitor implements IQuestionVisitor<AbstractQuestionPanel> {

    private int leftLabelWidth;

    public DisplayQuestionnaireVisitor(int leftLabelWidth) {
        this.leftLabelWidth = leftLabelWidth;
    }

    @Override
    public AbstractQuestionPanel Visit(ClosedButtonQuestion question) {
        return new ClosedButtonsPanel(question.getOptions().toArray(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(ClosedComboQuestion question) {
        return new ClosedComboPanel(question.getOptions().toArray(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(LikertQuestion question) {
        return new LikertPanel(question.getSize(), question.getLeft(), leftLabelWidth, question.getRight(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(SemDiffQuestion question) {
        return new SemDiffPanel(question.getSize(), question.getLeft(), leftLabelWidth, question.getRight(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(OpenQuestion question) {
        return new OpenQuestionPanel(question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(OpenTextAreaQuestion question) {
        return new TextAreaPanel(question.isRequired());
    }
}
