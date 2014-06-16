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

package Views.Questionnaire.QuestionEditPanels;

import DataStructures.Questionnaire.*;

/**
 * Created by marcel on 3/25/14.
 * Implementation of the Questionvisitor that returns the correct question edit panel
 */
public class EditQuestionVisitor implements IQuestionVisitor<AbstractQuestionEditPanel> {
    @Override
    public AbstractQuestionEditPanel Visit(ClosedButtonQuestion question) {
        return new ClosedQuestionEditPanel<ClosedButtonQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(ClosedComboQuestion question) {
        return new ClosedQuestionEditPanel<ClosedComboQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(LikertQuestion question) {
        return new LikertSemDiffQuestionEditPanel<LikertQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(SemDiffQuestion question) {
        return new LikertSemDiffQuestionEditPanel<SemDiffQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(OpenQuestion question) {
        return new OpenQuestionEditPanel<OpenQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(OpenTextAreaQuestion question) {
        return new OpenQuestionEditPanel<OpenTextAreaQuestion>(question);
    }
}
