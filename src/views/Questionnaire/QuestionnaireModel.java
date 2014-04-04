package views.Questionnaire;

import DataStructures.Questionnaire.*;

import java.util.*;

/**
 * Created by marcel on 3/25/14.
 */
public class QuestionnaireModel extends Observable {

    private AbstractQuestion original;
    private AbstractQuestion newQuestion;
    private QuestionEditFrame currentEditFrame;
    private int pos;
    public static final String[] Types = {"Likert Scale", "Semantic differential", "Closed question (combo-box)", "Closed question (buttons)", "Open question", "Open question with text area"};
    private static final AbstractQuestion[] QuestionClasses = {new LikertQuestion(),new SemDiffQuestion(),new ClosedComboQuestion(),new ClosedButtonQuestion(),new OpenQuestion(),new OpenTextAreaQuestion() };

    private HashMap<String,AbstractQuestion> questionMap;

    public QuestionnaireModel(AbstractQuestion question, int pos) {
        this.original = question;
        questionMap = new HashMap<String, AbstractQuestion>();
        fillQuestionMap();
        newQuestion = original;
        this.pos = pos;
    }

    private void fillQuestionMap() {
        if(questionMap.size()!=0) {
            for(int x = 0;x< Types.length;x++) {
                 questionMap.put(Types[x],QuestionClasses[x]);
            }
        }
    }

    public AbstractQuestion getOriginalQuestion() {
        return original;
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

    // public QuestionEditFrame changeFrameType(Enum EditFrameType)
}
