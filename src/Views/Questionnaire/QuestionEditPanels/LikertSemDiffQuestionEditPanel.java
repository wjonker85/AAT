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

/**
 * Created by marcel on 3/25/14.
 * lass used for the likert scale and the semantic differential scale. Basis properties are the same for both.
 */

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.AbstractScaleQuestion;
import DataStructures.Questionnaire.LikertQuestion;
import DataStructures.Questionnaire.SemDiffQuestion;

import javax.swing.*;
import java.awt.*;

public class LikertSemDiffQuestionEditPanel<T extends AbstractQuestion> extends AbstractQuestionEditPanel {
    private JTextField left, right, size;
    private AbstractScaleQuestion original;

    public LikertSemDiffQuestionEditPanel(AbstractScaleQuestion original) {
        super();
        this.original = original;
        left = new JTextField();
        JLabel leftLabel = new JLabel("Left label: ");
        left.setPreferredSize(new Dimension(120, 25));
        this.add(leftLabel);
        this.add(left);
        right = new JTextField();
        JLabel rightLabel = new JLabel("Right label: ");
        right.setPreferredSize(new Dimension(120, 25));
        this.add(rightLabel);
        this.add(right);
        size = new JTextField();
        JLabel sizeLabel = new JLabel("No options: ");
        this.add(sizeLabel);
        this.add(size);
        size.setPreferredSize(new Dimension(40, 25));

        addRequiredFields();
        if (original != null) {
            question.setText(original.getQuestion());
            left.setText(original.getLeft());
            right.setText(original.getRight());
            size.setText(String.valueOf(original.getSize()));
            qLabel.setText(original.getQuestion());
            required.setSelected(original.isRequired());
        }
        SpringUtilities.makeCompactGrid(this,
                6, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

    }

    @Override
    public T getQuestion() {

        AbstractScaleQuestion abstractQuestion = null;
        if (original instanceof LikertQuestion) {
            abstractQuestion = new LikertQuestion();
        } else if (original instanceof SemDiffQuestion) {
            abstractQuestion = new SemDiffQuestion();
        }
        assert abstractQuestion != null;
        abstractQuestion.setKey(qLabel.getText());
        abstractQuestion.setQuestion(question.getText());
        abstractQuestion.setLeft(left.getText());
        abstractQuestion.setRight(right.getText());
        abstractQuestion.setSize(Integer.parseInt(size.getText()));
        abstractQuestion.setRequired(required.isSelected());
        return (T) abstractQuestion;
    }

    @Override
    public boolean validated() {
        return true;
    }
}
