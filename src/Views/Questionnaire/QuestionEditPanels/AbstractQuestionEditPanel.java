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

import DataStructures.Questionnaire.AbstractQuestion;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 3/25/14.
 * Super class for a question editor
 */
public abstract class AbstractQuestionEditPanel<T extends AbstractQuestion> extends JPanel {

    public JTextField question, qLabel;
    public JCheckBox required;

    public AbstractQuestionEditPanel() {
        this.setLayout(new SpringLayout());
        question = new JTextField();
        qLabel = new JTextField();
        required = new JCheckBox();
    }

    public void addRequiredFields() {
        this.add(new JLabel("Question: "));
        question.setPreferredSize(new Dimension(250, 25));
        this.add(question);
        this.add(new JLabel("Label for analysis: "));
        this.add(qLabel);
        JLabel reqLabel = new JLabel("Required: ");
        this.add(reqLabel);
        required.setSelected(true);
        this.add(required);
    }


    public abstract T getQuestion();

    public abstract boolean validated();    //TODO unused, but might be implemented later
}
