package views.Questionnaire;

import views.Questionnaire.QuestionEditPanels.AbstractQuestionEditPanel;
import views.Questionnaire.QuestionEditPanels.EditQuestionVisitor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Created by marcel on 3/16/14.
 * Class containing question edit functionality.
 */

public class QuestionEditFrame extends JFrame {
    private String[] types = {"Likert Scale", "Semantic differential", "Closed question (combo-box)", "Closed question (buttons)", "Open question", "Open question with text area"};
    private HashMap<String, String> translations;
    private HashMap<String, String> translationsRev;
    private JComboBox typeCombo;
    private AbstractQuestionEditPanel editPanel;

    public QuestionEditFrame(final QuestionnaireModel questionnaireModel) {
        translations = new HashMap<String, String>();
        translationsRev = new HashMap<String, String>();
        translations.put("Likert Scale", "likert");
        translations.put("Closed question (combo-box)", "closed_combo");
        translations.put("Open question", "open");
        translations.put("Closed question (buttons)", "closed_button");
        translations.put("Semantic differential", "sem_diff");
        translations.put("Open question with text area", "textArea");
        translationsRev.put("likert", "Likert Scale");
        translationsRev.put("closed_combo", "Closed question (combo-box)");
        translationsRev.put("open", "Open question");
        translationsRev.put("closed_button", "Closed question (buttons)");
        translationsRev.put("sem_diff", "Semantic differential");
        translationsRev.put("textArea", "Open question with text area");
        JPanel setTypePanel = new JPanel();
        JLabel typeLabel = new JLabel("Type of question: ");
        setTypePanel.add(typeLabel);
        typeCombo = new JComboBox(types);             //TODO vervangen door visitor
        editPanel = questionnaireModel.getNewQuestion().Accept(new EditQuestionVisitor());
        this.add(editPanel);


        setTypePanel.add(typeCombo);
        JButton setTypeButton = new JButton("Set question type");
        setTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                questionnaireModel.setNewQuestion(editPanel.getQuestion());
                questionnaireModel.hasChanged();
                questionnaireModel.notifyObservers("type changed");

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
              //  if (editPanel.validated() && editPanel.getQuestion().getKey().length() > 0 && editPanel.getQuestion().getQuestion().length() > 0) {
                System.out.println(questionnaireModel.getPos()+" "+questionnaireModel.countObservers());
                questionnaireModel.setNewQuestion(editPanel.getQuestion());

           //   } else if (editPanel.getQuestion().getKey().length() == 0) {
            ///        JOptionPane.showConfirmDialog(null, "Question label needs to be set.", "Validation Error", JOptionPane.ERROR_MESSAGE);
             //   } else {
            //        if (editPanel.getQuestion().getQuestion().length() == 0) {
             //           JOptionPane.showConfirmDialog(null, "Question is empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
             //       }
               // }
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
