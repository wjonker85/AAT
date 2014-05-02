package views.Questionnaire.QuestionEditPanels;

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
