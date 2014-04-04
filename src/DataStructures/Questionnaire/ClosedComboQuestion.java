package DataStructures.Questionnaire;

import java.util.ArrayList;

/**
 * Created by marcel on 3/25/14.
 */

public class ClosedComboQuestion extends AbstractClosedQuestion {

    private ArrayList<String> options;

    public ClosedComboQuestion() {
        super();
        options = new ArrayList<String>();
    }

    @Override
    public <T> T Accept(IQuestionVisitor<T> visitor) {
        return visitor.Visit(this);
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public void Accept(IVoidQuestionVisitor visitor) {
        visitor.Visit(this);
    }

    @Override
    public AbstractQuestion newInstance() {
        return new ClosedComboQuestion();
    }

    @Override
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion) {
        return null;
    }
}
