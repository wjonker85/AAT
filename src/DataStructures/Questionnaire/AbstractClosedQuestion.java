package DataStructures.Questionnaire;

import java.util.ArrayList;

/**
 * Created by marcel on 3/25/14.
 * Abstract super class for an closed question.
 */
public abstract class AbstractClosedQuestion extends AbstractQuestion {

    private ArrayList<String> options;

    public AbstractClosedQuestion() {
        options = new ArrayList<String>();
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
