package views.Questionnaire;

import DataStructures.Questionnaire.*;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 * A model that is given to the different question edit panels. This model contains the original question as wel as the changed values and the position of the question
 * in the list. It uses a model view controller construction, so that views can listen to changes in this model.
 */
public class QuestionnaireModel extends Observable {

    private AbstractQuestion original;
    private AbstractQuestion newQuestion;
    private QuestionEditFrame currentEditFrame;
    private int pos;
    public static final String[] Types = {"Likert Scale", "Semantic differential", "Closed question (combo-box)", "Closed question (buttons)", "Open question", "Open question with text area"};
    private static final AbstractQuestion[] QuestionClasses = {new LikertQuestion(), new SemDiffQuestion(), new ClosedComboQuestion(), new ClosedButtonQuestion(), new OpenQuestion(), new OpenTextAreaQuestion()};

    private HashMap<String, AbstractQuestion> questionMap;

    public QuestionnaireModel(AbstractQuestion question, int pos) {
        this.original = question;
        questionMap = new HashMap<String, AbstractQuestion>();
        fillQuestionMap();
        newQuestion = original;
        this.pos = pos;
    }

    private void fillQuestionMap() {
        if (questionMap.size() == 0) {
            for (int x = 0; x < Types.length; x++) {
                questionMap.put(Types[x], QuestionClasses[x]);
                System.out.println("Adding " + Types[x]);
            }
        }
    }

    public AbstractQuestion getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(AbstractQuestion newQuestion) {
        if (!newQuestion.equals(original)) {
            this.newQuestion = newQuestion;
            this.setChanged();
            this.notifyObservers("submit");
        }
    }

    public void changeQuestionType(AbstractQuestion newQuestion, String to) {
        this.original = newQuestion;
        System.out.println("TO " + to);
        this.newQuestion = original.convertQuestion(questionMap.get(to).newInstance());
        System.out.println("model changed");
        this.setChanged();
        currentEditFrame.dispose();
        currentEditFrame = null;
        this.notifyObservers("type changed");

    }

    public QuestionEditFrame getEditFrame() {
        if (currentEditFrame == null) {
            currentEditFrame = new QuestionEditFrame(this);
        }
        return currentEditFrame;
    }

    public int getPos() {
        return pos;
    }

}
