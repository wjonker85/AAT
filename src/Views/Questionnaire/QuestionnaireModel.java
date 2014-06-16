/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package Views.Questionnaire;

import DataStructures.Questionnaire.*;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 * A model that is given to the different question edit panels. This model contains the original question as wel as the changed values and the position of the question
 * in the list. It uses a model view controller construction, so that Views can listen to changes in this model.
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
