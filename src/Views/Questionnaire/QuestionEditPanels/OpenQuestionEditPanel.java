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

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.OpenQuestion;
import DataStructures.Questionnaire.OpenTextAreaQuestion;

import javax.swing.*;

/**
 * Created by marcel on 3/25/14.
 * Editor panel for an open question
 */
class OpenQuestionEditPanel<T extends AbstractQuestion> extends AbstractQuestionEditPanel {

    private AbstractQuestion original;

    public OpenQuestionEditPanel(AbstractQuestion original) {
        this.setLayout(new SpringLayout());
        this.add(question);
        this.original = original;
        addRequiredFields();
        if (original != null) {
            question.setText(original.getQuestion());
            qLabel.setText(original.getQuestion());
            required.setSelected(original.isRequired());
        }
        SpringUtilities.makeCompactGrid(this,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
    }

    @Override
    public AbstractQuestion getQuestion() {
        AbstractQuestion abstractQuestion = null;
        if (original instanceof OpenQuestion) {
            abstractQuestion = new OpenQuestion();
        } else if (original instanceof OpenTextAreaQuestion) {
            abstractQuestion = new OpenTextAreaQuestion();
        }
        abstractQuestion.setKey(qLabel.getText());
        abstractQuestion.setQuestion(question.getText());
        abstractQuestion.setRequired(required.isSelected());
        return (T) abstractQuestion;
    }


    @Override
    public boolean validated() {
        return true;
    }

}

