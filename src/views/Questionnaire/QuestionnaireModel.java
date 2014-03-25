package views.Questionnaire;

import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.Questionnaire;

import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 */
public class QuestionnaireModel<T extends AbstractQuestion> extends Observable {

    private T question;
    private QuestionEditFrame currentEditFrame;
    private int pos;

    public QuestionnaireModel(T question, int pos)   {
        this.question = question;
        this.pos = pos;
    }

    public T getQuestion() {
        return question;
    }

    public QuestionEditFrame getEditFrame()
    {
         if(currentEditFrame == null) {
             currentEditFrame = new QuestionEditFrame(this);
         }
        return currentEditFrame;
    }

   // public QuestionEditFrame changeFrameType(Enum EditFrameType)
}
