package views.Questionnaire.QuestionEditPanels;

/**
 * Created by marcel on 3/25/14.
 */

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.AbstractScaleQuestion;
import DataStructures.Questionnaire.LikertQuestion;
import DataStructures.Questionnaire.SemDiffQuestion;

import javax.swing.*;
import java.awt.*;

/**
 * Class used for the likert scale and the semantic differential scale. Basis properties are the same for both.
 */
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
