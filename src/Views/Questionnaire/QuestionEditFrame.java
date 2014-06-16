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

import Views.Questionnaire.QuestionEditPanels.AbstractQuestionEditPanel;
import Views.Questionnaire.QuestionEditPanels.EditQuestionVisitor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by marcel on 3/16/14.
 * Class containing question edit functionality.
 */

public class QuestionEditFrame extends JFrame {

    private JComboBox<String> typeCombo;
    private AbstractQuestionEditPanel editPanel;

    public QuestionEditFrame(final QuestionnaireModel questionnaireModel) {
        JPanel setTypePanel = new JPanel();
        JLabel typeLabel = new JLabel("Type of question: ");
        setTypePanel.add(typeLabel);
        typeCombo = new JComboBox<String>(QuestionnaireModel.Types);
        typeCombo.setSelectedItem(questionnaireModel.getNewQuestion().Accept(new QuestionTypeVisitor()));
        this.setTitle(questionnaireModel.getNewQuestion().Accept(new QuestionTypeVisitor()));
        editPanel = questionnaireModel.getNewQuestion().Accept(new EditQuestionVisitor());
        this.add(editPanel);


        setTypePanel.add(typeCombo);
        JButton setTypeButton = new JButton("Change type of question");
        setTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Change question type");
                questionnaireModel.changeQuestionType(editPanel.getQuestion(), typeCombo.getSelectedItem().toString());
            }
        });
        setTypePanel.add(setTypeButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(setTypePanel);
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("OK button pressed");
                if (editPanel.getQuestion().getKey().length() > 0 && editPanel.getQuestion().getQuestion().length() > 0) {
                    System.out.println(questionnaireModel.getPos() + " " + questionnaireModel.countObservers());
                    questionnaireModel.setNewQuestion(editPanel.getQuestion());

                } else if (editPanel.getQuestion().getKey().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Question label needs to be set.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (editPanel.getQuestion().getQuestion().length() == 0) {
                        JOptionPane.showMessageDialog(null, "Question is empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        buttonPanel.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeAction();
            }
        });
        buttonPanel.add(cancelButton);
        mainPanel.add(editPanel);
        mainPanel.add(buttonPanel);
        this.getContentPane().add(mainPanel);
        this.pack();
    }

    private void closeAction() {
        this.dispose();
    }
}
