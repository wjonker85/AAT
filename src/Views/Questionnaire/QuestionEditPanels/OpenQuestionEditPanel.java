package Views.Questionnaire.QuestionEditPanels;

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.*;

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

