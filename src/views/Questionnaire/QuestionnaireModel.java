package views.Questionnaire;

import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.Questionnaire;

import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 */
public class QuestionnaireModel<T extends AbstractQuestion> extends Observable {

    private T original,newQuestion;
    private QuestionEditFrame currentEditFrame;
    private int pos;

    public QuestionnaireModel(T question, int pos)   {
        this.original = question;
        newQuestion = original;
        this.pos = pos;
    }

    public T getOriginalQuestion() {
        return original;
    }

    public T getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(T newQuestion) {
        if(!newQuestion.equals(original)) {
            this.newQuestion = newQuestion;
            this.setChanged();
            this.notifyObservers("submit");
        }
    }

    public QuestionEditFrame getEditFrame()
    {
         if(currentEditFrame == null) {
             currentEditFrame = new QuestionEditFrame(this);
         }
        return currentEditFrame;
    }

    public int getPos() {
        return pos;
    }

   // public QuestionEditFrame changeFrameType(Enum EditFrameType)
}
