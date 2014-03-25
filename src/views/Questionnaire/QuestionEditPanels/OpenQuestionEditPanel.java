package views.Questionnaire.QuestionEditPanels;

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.OpenQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 */
class OpenQuestionEditPanel<T>  extends AbstractQuestionEditPanel {
    private JTextField question, qLabel;
    private JCheckBox required;

    public OpenQuestionEditPanel(AbstractQuestion original) {
        this.setLayout(new SpringLayout());
        this.add(new JLabel("Question: "));
        question = new JTextField();
        question.setPreferredSize(new Dimension(250, 20));
        this.add(question);
        this.add(new JLabel("Label for analysis: "));
        qLabel = new JTextField();
        this.add(qLabel);
        JLabel reqLabel = new JLabel("Required: ");
        this.add(reqLabel);
        required = new JCheckBox();

        required.setSelected(true);
        this.add(required);
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
        AbstractQuestion abstractQuestion = new OpenQuestion();
        abstractQuestion.setKey(qLabel.getText());
        abstractQuestion.setQuestion(question.getText());
        abstractQuestion.setRequired(required.isSelected());
        return abstractQuestion;
    }

    @Override
    public boolean validated() {
        return false;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

