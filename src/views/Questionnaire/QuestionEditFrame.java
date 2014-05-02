package views.Questionnaire;

import views.Questionnaire.QuestionEditPanels.AbstractQuestionEditPanel;
import views.Questionnaire.QuestionEditPanels.EditQuestionVisitor;

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
