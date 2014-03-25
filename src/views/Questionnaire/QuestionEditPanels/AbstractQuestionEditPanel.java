package views.Questionnaire.QuestionEditPanels;

import DataStructures.Questionnaire.AbstractQuestion;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by marcel on 3/25/14.
 */
public abstract class AbstractQuestionEditPanel<T extends AbstractQuestion> extends JPanel implements Observer{

    public AbstractQuestionEditPanel() {
        this.setLayout(new SpringLayout());
        this.add(new JLabel("Question: "));
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    public abstract T getQuestion();

    public abstract boolean validated();
}
